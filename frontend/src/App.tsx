import { invoke } from "@tauri-apps/api/core";
import { useCallback, useState } from "react";
import {
  Background,
  Controls,
  MiniMap,
  ReactFlow,
  useNodesState,
  useEdgesState,
  applyNodeChanges,
  applyEdgeChanges,
  Edge,
} from "@xyflow/react";

import "@xyflow/react/dist/style.css";

import { nodeTypes, addNodes, AppNode } from "./nodes";
import { edgeTypes, addEdges } from "./edges";

export default function App() {
  const [nodes, setNodes] = useNodesState<AppNode>([]);
  const [edges, setEdges] = useEdgesState<Edge>([]);
  const [regex, setRegex] = useState("");
  const [input, setInput] = useState("");
  const onNodesChange = useCallback(
    (changes: any) => setNodes((nodesSnapshot) => applyNodeChanges(changes, nodesSnapshot)),
    [],
  );
  const onEdgesChange = useCallback(
    (changes: any) => setEdges((edgesSnapshot) => applyEdgeChanges(changes, edgesSnapshot)),
    [],
  );

  // communication with the backend code

  // give the backend our request and wait for the java backend to process and return a json string and set nodes using that
  async function convertRegex(regex: string) {

    const request = {
      command: "build",
      regex: regex,
      input: ""
    }

    try {
      const response = await invoke('backend', { req: JSON.stringify(request) });
      const json = JSON.parse(String(response));

      if (json.error) {
        alert(String(json.error));
        return;
      }

      const newNodes = addNodes(json.states, json.transitions);
      setNodes(newNodes);
      const newEdges = addEdges(json.transitions);
      setEdges(newEdges);
      console.log(json.states);
      console.log(newNodes);

    } catch (error) {
      console.error(error);
      alert(String(error));
    }
    
  };

  // give the backend our request and wait for the java backend to process and return a json string and set nodes using that
  async function processInput(regex: string, input: string) {

    const request = {
      command: "simulate",
      regex: regex,
      input: input
    }

    try {
      const response = await invoke('backend', { req: JSON.stringify(request) });
      const json = JSON.parse(String(response));

        if (json.error) {
          alert(String(json.error));
          return;
        }
        
      // update active nodes with the relevant properties so the css can highlight them appropriately
      for (const step of json.steps) {
        setNodes(nodes => (nodes.map(node => ({...node, data: {...node.data, active: step.activeStates.includes(Number(node.id)), failed: false}}))));
        await sleep(500);
      }

      if (json.accepted) {

        // highlight accept node 
        setNodes(nodes => (nodes.map(node => ({...node, data: {...node.data, active: node.data.accept, failed: false}}))));

      } else {
        setNodes(nodes => (nodes.map(node => ({...node, data: {...node.data, failed: node.data.active, active: false}}))));
      }


    } catch (error) {
      console.error("BACKEND ERROR:", error);
      alert(`Backend error: ${error}`);
    }

  };

  function sleep(ms: number) {
    return new Promise(resolve => setTimeout(resolve, ms));
  }

  return (
    <div className = "app">
      <div className = "flow">
        <ReactFlow
          nodes={nodes}
          nodeTypes={nodeTypes}
          onNodesChange={onNodesChange}
          edges={edges}
          edgeTypes={edgeTypes}
          onEdgesChange={onEdgesChange}
          fitView
        >
          <Background />
          <MiniMap />
          <Controls />
        </ReactFlow>
      </div>

      <div className = "bottom-panel">
        <form className = "input" onSubmit = {(exp) => {exp.preventDefault(); convertRegex(regex);}}>
          <label htmlFor = "regex">Regex </label>
          <input id = "regex" type = "text" value = {regex} onChange = {(exp) => setRegex(exp.target.value)}/>
          <button type = "submit">Submit</button>
        </form>

        <form className = "input" onSubmit = {(inp) => {inp.preventDefault(); processInput(regex, input);}}>
          <label htmlFor = "input">Input </label>
          <input id = "input" type = "text" value = {input} onChange = {(inp) => setInput(inp.target.value)}/>
          <button type = "submit">Submit</button>
        </form>
      </div>
    </div>
  );
}