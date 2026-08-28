import type { Edge, EdgeTypes } from "@xyflow/react";
import SelfConnecting from "./SelfConnectingEdge";

export function addEdges(
  transitions: {start: number, end: number, symbol: string}[]
) {

  const edges: Edge[] = [];
  for (const transition of transitions) {

    const existingEdge = edges.find((edge) => edge.source == String(transition.start) && edge.target == String(transition.end))

    // combine labels for parallel edges
    if (existingEdge) {
      existingEdge.label += `|${transition.symbol}`;
    } else {
      const edge: Edge = {
        id: `${transition.start}-${transition.end}-${transition.symbol}`,
        source: String(transition.start),
        target: String(transition.end),
        animated: true,
        type: (transition.start == transition.end) ? "selfconnecting" : "smoothstep",
        label: String(transition.symbol)
      };

      edges.push(edge);
    }
  }

  return edges;
}

export const edgeTypes = {
  selfconnecting: SelfConnecting
} satisfies EdgeTypes;
