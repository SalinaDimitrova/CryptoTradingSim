import { Link } from "react-router-dom";

const Navbar = () => (
  <nav className="bg-gray-900 text-white p-4 flex justify-between">
    <div className="text-xl font-bold">CryptoSim</div>
    <div className="space-x-4">
      <Link to="/" className="hover:underline">Home</Link>
      <Link to="/portfolio" className="hover:underline">Portfolio</Link>
      <Link to="/history" className="hover:underline">History</Link>
    </div>
  </nav>
);

export default Navbar;
