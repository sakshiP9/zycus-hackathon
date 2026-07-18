import axiosInstance from './axiosInstance';

export async function fetchOrders(status) {
  const params = status ? { status } : {};
  const { data } = await axiosInstance.get('/order', { params });
  return data;
}
