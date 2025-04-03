import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar';
import Home from './pages/Home';  // Home page (top 20 cryptos)
import Portfolio from './pages/Portfolio';  // Portfolio page
import History from './pages/History';  // History page
import CryptoList from './components/CryptoList';  // Import CryptoList

function App() {
  return (
    <Router>
      <Navbar />
      <div className="p-4">
        <Routes>
          <Route path="/" element={<Home />} />  {/* Home page: Top 20 crypto prices */}
          <Route path="/portfolio" element={<Portfolio />} />  {/* Portfolio page */}
          <Route path="/history" element={<History />} />  {/* Transaction History page */}
          <Route path="/cryptos" element={<CryptoList />} />  {/* Add a route for CryptoList */}
        </Routes>
      </div>
    </Router>
  );
}

export default App;
