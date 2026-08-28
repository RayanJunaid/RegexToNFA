import type { Node, NodeTypes } from "@xyflow/react";
import { StateNodeData, StateNode } from "./StateNode.tsx";

export type AppNode = Node<StateNodeData, "state-node">;

export function addNodes(
  states: {id: number, start: boolean, accept: boolean}[],
  transitions: {start: number, end: number, symbol: string}[]
) {

  const nodes: AppNode[] = [];
  const startState = states.find(state => state.start);
  if (!startState) {return nodes};

  const orderedStates = [];
  const visited = new Set<number>();

  // order nodes using their transitions, follows one transition path
  let curr = startState.id;
  while (!visited.has(curr)) {

    visited.add(curr);
    const state = states.find(state => state.id == curr);
    if (!state) {break;}
    orderedStates.push(state);

    // find a transition from the current state to another unvisited state
    // need to ignore self loops here of any form
    const transition = transitions.find(tra => tra.start == curr && tra.end != curr && !visited.has(tra.end));
    if (!transition) {break;}
    curr = transition.end;

  }

  // add the remaining nodes not reached by the transition path above
  for (const state of states) {
    if (!visited.has(state.id)) {
      orderedStates.push(state);
    }
  }

  for (let i = 0; i < orderedStates.length; i++) {

    const state = orderedStates[i];
    const node: AppNode = {
      id: String(state.id),
      type: "state-node",
      position: {x: 200 * i, y: 0},
      data: {start: state.start, accept: state.accept, active: false, failed: false}
    };

    nodes.push(node);
  }

  return nodes;

}

export const nodeTypes = {
  "state-node": StateNode,
} satisfies NodeTypes;

