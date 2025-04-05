import { Link } from 'react-router-dom';

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

export default NavBar;

