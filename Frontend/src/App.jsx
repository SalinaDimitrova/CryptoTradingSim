// App.jsx

import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';

// --- NavBar ---
const NavBar = () => (
  <nav className="bg-gray-900/80 backdrop-blur-md shadow-md fixed top-0 left-0 w-full z-50">
    <div className="max-w-7xl mx-auto px-6 py-4 flex justify-between items-center">
      <h1 className="text-2xl font-extrabold text-white tracking-tight">CryptoDash</h1>
      <div className="flex space-x-6 text-base">
        {["Home", "Portfolio", "History"].map((item, i) => (
          <Link
            key={i}
            to={item === "Home" ? "/" : `/${item.toLowerCase()}`}
            className="text-gray-300 hover:text-white hover:underline underline-offset-4 transition-all duration-200"
          >
            {item}
          </Link>
        ))}
      </div>
    </div>
  </nav>
);

// --- Home Page ---
const Home = () => (
  <section className="min-h-screen bg-gradient-to-br from-gray-900 via-black to-gray-800 text-white pt-28 px-4">
    <div className="max-w-4xl mx-auto text-center">
      <h1 className="text-5xl sm:text-6xl font-extrabold mb-6 tracking-tight leading-tight">
        Commission-Free Crypto Investing
      </h1>
      <p className="text-gray-400 text-lg mb-10 max-w-2xl mx-auto">
        Start investing with real-time insights and no hidden fees.
      </p>
      <Link to="/open-account">
        <button className="bg-blue-600 hover:bg-blue-700 text-white px-10 py-3 rounded-full text-lg font-semibold transition duration-300 ease-in-out shadow-lg hover:shadow-xl">
          🚀 Place an Order
        </button>
      </Link>
    </div>
  </section>
);

// --- OpenAccount Page ---
const OpenAccount = () => (
  <section className="min-h-screen bg-black pt-28 px-4 text-white">
    <div className="max-w-md mx-auto bg-gray-900/70 backdrop-blur-md p-8 rounded-2xl shadow-2xl">
      <h2 className="text-3xl font-bold mb-6 text-center">📝 Place a Crypto Order</h2>
      <form className="space-y-5">
        <input
          type="text"
          placeholder="Crypto Symbol"
          className="w-full px-4 py-3 rounded-lg bg-gray-800 border border-gray-700 placeholder-gray-500 text-white focus:ring-2 focus:ring-blue-600 focus:outline-none transition"
        />
        <input
          type="number"
          placeholder="Quantity"
          className="w-full px-4 py-3 rounded-lg bg-gray-800 border border-gray-700 placeholder-gray-500 text-white focus:ring-2 focus:ring-blue-600 focus:outline-none transition"
        />
        <input
          type="number"
          placeholder="Price (USD)"
          className="w-full px-4 py-3 rounded-lg bg-gray-800 border border-gray-700 placeholder-gray-500 text-white focus:ring-2 focus:ring-blue-600 focus:outline-none transition"
        />
        <select className="w-full px-4 py-3 rounded-lg bg-gray-800 border border-gray-700 text-white">
          <option>Buy</option>
          <option>Sell</option>
        </select>
        <button className="w-full bg-blue-600 hover:bg-blue-700 py-3 rounded-lg font-semibold transition duration-200 shadow-md hover:shadow-lg">
          ✅ Confirm Order
        </button>
      </form>
    </div>
  </section>
);

// --- Portfolio Page ---
const Portfolio = () => (
  <section className="pt-28 pb-20 bg-gray-950 text-white min-h-screen px-4">
    <div className="max-w-5xl mx-auto">
      <h1 className="text-3xl font-bold mb-4 text-center">📊 Your Portfolio</h1>
      <p className="text-gray-400 text-center mb-10">
        Overview of your crypto assets, gains, and losses.
      </p>
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {[1, 2, 3].map((id) => (
          <div
            key={id}
            className="bg-gradient-to-br from-gray-900 to-gray-800 border border-gray-700 rounded-xl p-6 shadow-md hover:shadow-2xl transition duration-300"
          >
            <h3 className="text-xl font-semibold mb-2">Bitcoin (BTC)</h3>
            <p className="text-gray-400">Amount: 0.75 BTC</p>
            <p className="text-green-400 mt-2">+12% ($3,200)</p>
          </div>
        ))}
      </div>
    </div>
  </section>
);

// --- History Page ---
const History = () => (
  <section className="pt-28 pb-20 bg-gray-950 text-white min-h-screen px-4">
    <div className="max-w-5xl mx-auto">
      <h1 className="text-3xl font-bold mb-4 text-center">📜 Transaction History</h1>
      <p className="text-gray-400 text-center mb-10">
        View your previous orders and trade performance.
      </p>
      <div className="overflow-x-auto">
        <table className="min-w-full table-auto border-collapse text-left">
          <thead>
            <tr className="bg-gray-800 text-sm uppercase text-gray-400">
              {["Date", "Crypto", "Type", "Amount", "Value (USD)"].map((col, i) => (
                <th key={i} className="px-6 py-3 border-b border-gray-700">{col}</th>
              ))}
            </tr>
          </thead>
          <tbody>
            <tr className="hover:bg-gray-800 transition-all duration-200">
              <td className="px-6 py-4 border-b border-gray-700">2025-04-01</td>
              <td className="px-6 py-4 border-b border-gray-700">BTC</td>
              <td className="px-6 py-4 border-b border-gray-700">Buy</td>
              <td className="px-6 py-4 border-b border-gray-700">0.1</td>
              <td className="px-6 py-4 border-b border-gray-700">$6,200</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </section>
);

// --- App Wrapper ---
const App = () => (
  <div className="bg-black text-white font-sans">
    <Router>
      <NavBar />
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/portfolio" element={<Portfolio />} />
        <Route path="/history" element={<History />} />
        <Route path="/open-account" element={<OpenAccount />} />
      </Routes>
    </Router>
  </div>
);

export default App;
