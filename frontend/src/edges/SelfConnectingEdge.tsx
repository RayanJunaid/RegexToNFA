import { BaseEdge, EdgeLabelRenderer, type EdgeProps } from '@xyflow/react';
 
export default function SelfConnecting(props: EdgeProps) {
 
  const { sourceX, sourceY, targetX, targetY, markerEnd, label } = props;
  const radiusX = (sourceX - targetX) * 0.6;
  const radiusY = 40;
  const edgePath = `M ${sourceX - 5} ${sourceY} A ${radiusX} ${radiusY} 0 1 0 ${
    targetX + 2
  } ${targetY}`;
  const labelX = (sourceX + targetX) / 2;
  const labelY = Math.min(sourceY, targetY) - 1.9 * radiusY;
 
  return (
    <>
        <BaseEdge path={edgePath} markerEnd={markerEnd} />
        <EdgeLabelRenderer> 
            <div style={{
                position: "absolute",
                transform: `translate(-50%, 0%) translate(${labelX}px, ${labelY}px)`,
                background: "white",
                padding: "2px 5px",
                borderRadius: "3px",
                fontSize: "10px"
                }} className="nodrag nopan">
                {label}
            </div>
        </EdgeLabelRenderer>
    </>
  );
}