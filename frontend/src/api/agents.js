import axiosInstance from './axiosInstance';

export async function fetchAgents() {
  const { data } = await axiosInstance.get('/agents');
  return data;
}

export async function updateAgentStatus(id, status) {
  const { data } = await axiosInstance.patch(`/agents/${id}/status`, { status });
  return data;
}
