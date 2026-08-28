import { Handle, Position, type NodeProps } from "@xyflow/react";

export type StateNodeData = {
  
    start: boolean,
    accept: boolean,
    active: boolean,
    failed: boolean
  
}

export function StateNode({ data, selected }: NodeProps) {

  return (
    <div className={`state-node ${data.accept ? "accept" : ""} ${selected ? "selected" : ""} ${data.active ? "active" : ""} ${data.failed ? "failed" : ""}`}>

      {!data.accept && (
        <Handle type="source" position={Position.Right} /> )}

      {!data.start && (
        <Handle type="target" position={Position.Left} /> )}

    </div>
  );

}
