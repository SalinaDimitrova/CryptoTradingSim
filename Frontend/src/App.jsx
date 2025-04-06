// App.jsx
import { useEffect, useRef, useState } from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';
import Home from './pages/Home';
import Portfolio from './pages/Portfolio';
import History from './pages/History';

const NavBar = () => (
  <nav className="bg-gray-900/80 backdrop-blur-md shadow-md fixed top-0 left-0 w-full z-50">
    <div className="max-w-7xl mx-auto px-6 py-4 flex justify-between items-center">
      <h1 className="text-2xl font-extrabold text-white tracking-tight">TradeSim</h1>
      <div className="flex space-x-6 text-base">
        {['Home', 'Portfolio', 'History'].map((item, i) => (
          <Link
            key={i}
            to={item === 'Home' ? '/' : `/${item.toLowerCase()}`}
            className="text-gray-300 hover:text-white hover:underline underline-offset-4 transition-all duration-200"
          >
            {item}
          </Link>
        ))}
      </div>
    </div>
  </nav>
);

const App = () => {
  const [prices, setPrices] = useState({});
  const stompClientRef = useRef(null);

  useEffect(() => {
    const socket = new SockJS('http://localhost:8080/ws');
    const stompClient = Stomp.over(socket);
    stompClientRef.current = stompClient;

    stompClient.connect({}, () => {
      stompClient.subscribe('/topic/prices', (message) => {
        const data = JSON.parse(message.body);
        if (Array.isArray(data)) {
          const updatedPrices = data.reduce((acc, item) => {
            if (item.symbol && item.last !== undefined) {
              acc[item.symbol] = parseFloat(item.last);
            }
            return acc;
          }, {});
          setPrices(prev => ({ ...prev, ...updatedPrices }));
        }
      });
    });

    return () => {
      stompClient.disconnect();
    };
  }, []);

  return (
    <div className="bg-black text-white font-sans">
      <Router>
        <NavBar />
        <Routes>
          <Route path="/" element={<Home prices={prices} />} />
          <Route path="/portfolio" element={<Portfolio prices={prices} />} />
          <Route path="/history" element={<History />} />
        </Routes>
      </Router>
    </div>
  );
};

export default App;
