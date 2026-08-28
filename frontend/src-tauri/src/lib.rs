use std::io::{BufRead, BufReader, Write};
use std::process::{Child, ChildStdin, ChildStdout, Command, Stdio};
use std::sync::Mutex;

use tauri::Manager;

struct Backend {
    _child: Child,
    stdin: ChildStdin,
    stdout: BufReader<ChildStdout>,
}

fn start_backend(app: &tauri::AppHandle) -> Result<Backend, String> {
    let jar = if cfg!(debug_assertions) {
        // Development
        std::path::PathBuf::from("../../backend/app/build/libs/app.jar")
    } else {
        // Release
        app.path()
            .resource_dir()
            .map_err(|e| e.to_string())?
            .join("resources")
            .join("app.jar")
    };

    println!("JAR PATH: {:?}", jar);
    println!("JAR EXISTS: {}", jar.exists());

    if !jar.exists() {
        return Err(format!("JAR file not found: {:?}", jar));
    }

    let mut child = Command::new("java")
        .args(["-jar"])
        .arg(&jar)
        .stdin(Stdio::piped())
        .stdout(Stdio::piped())
        .stderr(Stdio::inherit())
        .spawn()
        .map_err(|e| {
            format!(
                "Failed to start Java: {}\nJAR path: {:?}",
                e, jar
            )
        })?;

    let stdin = child
        .stdin
        .take()
        .ok_or("Failed to open Java stdin")?;

    let stdout = child
        .stdout
        .take()
        .ok_or("Failed to open Java stdout")?;

    Ok(Backend {
        _child: child,
        stdin,
        stdout: BufReader::new(stdout),
    })
}

#[tauri::command]
fn backend(
    app: tauri::AppHandle,
    state: tauri::State<'_, Mutex<Option<Backend>>>,
    req: String,
) -> Result<String, String> {

    let mut backend = state
        .lock()
        .map_err(|e| format!("Failed to lock backend: {}", e))?;

    // Start Java the first time this command is called.
    if backend.is_none() {
        *backend = Some(start_backend(&app)?);
    }

    let backend = backend
        .as_mut()
        .ok_or("Backend was not started")?;

    // Send request to Java
    backend
        .stdin
        .write_all(format!("{}\n", req).as_bytes())
        .map_err(|e| format!("Failed to write to Java: {}", e))?;

    backend
        .stdin
        .flush()
        .map_err(|e| format!("Failed to flush Java stdin: {}", e))?;

    // Wait for one response line
    let mut response = String::new();

    backend
        .stdout
        .read_line(&mut response)
        .map_err(|e| format!("Failed to read Java response: {}", e))?;

    if response.is_empty() {
        return Err("Java backend closed unexpectedly".to_string());
    }

    Ok(response.trim_end().to_string())
}

#[cfg_attr(mobile, tauri::mobile_entry_point)]
pub fn run() {
    tauri::Builder::default()
        .plugin(tauri_plugin_opener::init())

        .manage(Mutex::<Option<Backend>>::new(None))

        .invoke_handler(tauri::generate_handler![backend])

        .run(tauri::generate_context!())
        .expect("error while running tauri application");
}