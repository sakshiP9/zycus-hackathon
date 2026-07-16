import { useEffect, useState } from 'react'
import reactLogo from './assets/react.svg'
import viteLogo from './assets/vite.svg'
import heroImg from './assets/hero.png'
import './App.css'
import axiosInstance from './api/axiosInstance'

function App() {
  const [status, setStatus] = useState('checking...');

  useEffect(() => {
    axiosInstance.get('/health')
      .then((res) => setStatus(res.data))
      .catch((err) => setStatus('Error: ' + err.message));
  }, []);

  return <div>{status}</div>;
}

export default App;
