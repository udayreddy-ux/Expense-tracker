import axios from "axios";

const API = axios.create({
  //baseURL: process.env.REACT_APP_API_BASE_URL,
  baseURL: "http://localhost:8080/api",
});
console.log("ENV URL:", process.env.REACT_APP_API_BASE_URL);

API.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default API;
