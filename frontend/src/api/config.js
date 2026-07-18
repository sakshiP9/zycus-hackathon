import axiosInstance from './axiosInstance';

export async function fetchRoutingStrategy() {
  const { data } = await axiosInstance.get('/config/routing-strategy');
  return data;
}

export async function updateRoutingStrategy(strategy) {
  const { data } = await axiosInstance.patch('/config/routing-strategy', { strategy });
  return data;
}
