import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import api from '../api/axiosConfig';

interface Product {
  id: string;
  nombre: string;
  descripcion?: string;
  precio: number;
  cantidad: number;
  imagen?: string;
  categoria?: string;
}

export default function ProductList() {
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchProducts();
  }, []);

  const fetchProducts = async () => {
    try {
      const response = await api.get('/productos');
      setProducts(response.data.data);
    } catch (error) {
      console.error("Error al obtener productos:", error);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id: string) => {
    if (!window.confirm("¿Estás seguro de eliminar este producto?")) return;
    try {
      await api.delete(`/productos/${id}`);
      setProducts(products.filter(p => p.id !== id));
      alert("Producto eliminado correctamente.");
    } catch (error: any) {
      alert(error.response?.data?.message || "Error al eliminar el producto.");
    }
  };

  if (loading) return (
    <div className="flex justify-center items-center h-64">
      <div className="text-xl animate-pulse tracking-widest">CARGANDO SISTEMA...</div>
    </div>
  );

  return (
    <div>
      <div className="flex flex-col sm:flex-row justify-between items-center mb-10 gap-4">
        <h1 className="text-3xl uppercase tracking-widest text-white border-l-4 border-white pl-4">Catalogo de Juegos</h1>
        <Link to="/productos/nuevo" className="bg-transparent border border-white text-white px-6 py-2 hover:bg-white hover:text-black transition-colors uppercase text-sm tracking-tighter">
          Agregar Item
        </Link>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-8">
        {products.map((product) => (
          <div key={product.id} className="bg-gray-800 border border-gray-700 hover:border-white transition-colors group flex flex-col h-full">
            <div className="aspect-[4/3] bg-gray-900 overflow-hidden border-b border-gray-700 group-hover:border-white">
              <img 
                src={product.imagen || 'https://via.placeholder.com/300x225?text=NO+IMAGE'} 
                alt={product.nombre}
                className="w-full h-full object-cover opacity-80 group-hover:opacity-100 transition-opacity"
              />
            </div>
            <div className="p-4 flex-1 flex flex-col">
              <div className="flex justify-between items-start mb-2">
                <h2 className="text-lg leading-tight uppercase tracking-tight">{product.nombre}</h2>
                <span className="text-xs bg-gray-700 px-2 py-0.5 border border-gray-600 uppercase">{product.categoria || 'Juego'}</span>
              </div>
              <p className="text-gray-400 text-xs mb-4 line-clamp-2 flex-1">
                {product.descripcion || 'Sin descripción disponible para este título clásico.'}
              </p>
              <div className="flex justify-between items-end mt-auto">
                <div>
                  <div className="text-2xl text-white tracking-tight">${product.precio.toFixed(2)}</div>
                  <div className="text-[10px] text-gray-500 uppercase">Stock: {product.cantidad} unidades</div>
                </div>
                <button 
                  onClick={() => handleDelete(product.id)} 
                  className="text-gray-500 hover:text-red-500 text-[10px] uppercase border-b border-transparent hover:border-red-500 transition-all"
                >
                  [ Eliminar ]
                </button>
              </div>
            </div>
          </div>
        ))}
      </div>

      {products.length === 0 && (
        <div className="text-center py-20 border-2 border-dashed border-gray-800">
          <p className="text-gray-500 uppercase tracking-widest">No hay items en el inventario</p>
        </div>
      )}
    </div>
  );
}
