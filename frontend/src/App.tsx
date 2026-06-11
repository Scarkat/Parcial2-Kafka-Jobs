import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import ProductList from './pages/ProductList';
import ProductCreate from './pages/ProductCreate';
import OrderCreate from './pages/OrderCreate';
import OrderList from './pages/OrderList';
import OrderDetail from './pages/OrderDetail';

function App() {
  return (
    <Router>
      <div className="min-h-screen bg-gray-900 text-gray-100">
        <nav className="bg-gray-800 border-b border-gray-700">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
            <div className="flex justify-between h-16">
              <div className="flex space-x-8 items-center">
                <Link to="/productos" className="text-white text-xl tracking-widest">
                  VIDEOJUEGOS <span className="text-gray-400">RETRO</span>
                </Link>
                <div className="hidden sm:flex sm:space-x-4">
                  <Link to="/productos" className="text-gray-300 hover:text-white hover:bg-gray-700 px-3 py-1 border border-transparent hover:border-gray-500 transition-all text-sm uppercase tracking-tight">Catalogo</Link>
                  <Link to="/ordenes/nuevo" className="text-gray-300 hover:text-white hover:bg-gray-700 px-3 py-1 border border-transparent hover:border-gray-500 transition-all text-sm uppercase tracking-tight">Nueva Orden</Link>
                  <Link to="/ordenes" className="text-gray-300 hover:text-white hover:bg-gray-700 px-3 py-1 border border-transparent hover:border-gray-500 transition-all text-sm uppercase tracking-tight">Historial</Link>
                </div>
              </div>
            </div>
          </div>
        </nav>

        <main className="max-w-7xl mx-auto py-8 px-4 sm:px-6 lg:px-8">
          <Routes>
            <Route path="/" element={<ProductList />} />
            <Route path="/productos" element={<ProductList />} />
            <Route path="/productos/nuevo" element={<ProductCreate />} />
            <Route path="/ordenes" element={<OrderList />} />
            <Route path="/ordenes/nuevo" element={<OrderCreate />} />
            <Route path="/ordenes/:id" element={<OrderDetail />} />
          </Routes>
        </main>
      </div>
    </Router>
  );
}

export default App;
