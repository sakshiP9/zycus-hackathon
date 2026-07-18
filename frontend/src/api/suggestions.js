import axiosInstance from './axiosInstance';

export async function fetchSuggestions() {
  const { data } = await axiosInstance.get('/suggestions');
  return data;
}

export async function updateSuggestionStatus(id, status) {
  const { data } = await axiosInstance.patch(`/suggestions/${id}`, { status });
  return data;
}
