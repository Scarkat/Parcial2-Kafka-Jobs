import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/axiosConfig';

interface Product {
  id: string;
  nombre: string;
  precio: number;
  cantidad: number;
  imagen?: string;
}

interface OrderItem {
  codigoProducto: string;
  nombreProducto: string;
  precio: number;
  cantidad: number;
}

export default function OrderCreate() {
  const navigate = useNavigate();
  const [products, setProducts] = useState<Product[]>([]);
  const [selectedProducts, setSelectedProducts] = useState<OrderItem[]>([]);
  const [email, setEmail] = useState('');
  const [orderCode, setOrderCode] = useState(`ORD-${Date.now()}`);

  useEffect(() => {
    fetchProducts();
  }, []);

  const fetchProducts = async () => {
    try {
      const response = await api.get('/productos');
      setProducts(response.data.data);
    } catch (error) {
      console.error("Error al obtener productos:", error);
    }
  };

  const addProductToOrder = (product: Product) => {
    const existing = selectedProducts.find(p => p.codigoProducto === product.id);
    if (existing) return;

    setSelectedProducts([...selectedProducts, {
      codigoProducto: product.id,
      nombreProducto: product.nombre,
      precio: product.precio,
      cantidad: 1
    }]);
  };

  const updateQuantity = (index: number, qty: number) => {
    const updated = [...selectedProducts];
    updated[index].cantidad = qty;
    setSelectedProducts(updated);
  };

  const removeProduct = (index: number) => {
    setSelectedProducts(selectedProducts.filter((_, i) => i !== index));
  };

  const calculateTotal = () => {
    return selectedProducts.reduce((sum, p) => sum + (p.precio * p.cantidad), 0);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (selectedProducts.length === 0) {
      alert("Debe agregar al menos un producto a la orden.");
      return;
    }

    if (selectedProducts.some(p => p.cantidad <= 0)) {
      alert("La cantidad de todos los productos debe ser mayor a cero.");
      return;
    }

    const payload = {
      codigoOrden: orderCode,
      usuarioCorreo: email,
      productos: selectedProducts
    };

    try {
      await api.post('/ordenes', payload);
      alert("Orden registrada en el sistema.");
      navigate('/productos');
    } catch (error: any) {
      alert(error.response?.data?.message || "Error al procesar la orden.");
    }
  };

  const inputClass = "mt-1 block w-full bg-gray-900 border border-gray-700 focus:border-white focus:ring-0 text-gray-100 p-2 transition-colors uppercase text-xs";
  const labelClass = "block text-[10px] uppercase tracking-widest text-gray-400 mb-1";

  return (
    <div className="max-w-6xl mx-auto">
      <h1 className="text-2xl uppercase tracking-widest mb-10 border-l-4 border-white pl-4">Procesar Nueva Orden</h1>
      
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-10">
        {/* Selección de Productos */}
        <div className="lg:col-span-1 bg-gray-800 border border-gray-700 p-6 h-fit">
          <h2 className="text-sm uppercase tracking-widest mb-6 text-white border-b border-gray-700 pb-2">Seleccionar Titulos</h2>
          <div className="space-y-3 overflow-y-auto max-h-[500px] pr-2 custom-scrollbar">
            {products.map(p => (
              <div key={p.id} className="flex gap-3 items-center p-2 border border-gray-700 hover:border-gray-500 transition-colors group">
                <div className="w-12 h-12 bg-gray-900 border border-gray-700 flex-shrink-0 overflow-hidden">
                  <img src={p.imagen || 'https://via.placeholder.com/50'} alt={p.nombre} className="w-full h-full object-cover opacity-70 group-hover:opacity-100" />
                </div>
                <div className="flex-1 min-w-0">
                  <p className="text-[10px] uppercase truncate">{p.nombre}</p>
                  <p className="text-[9px] text-gray-500 uppercase">${p.precio} | STOCK: {p.cantidad}</p>
                </div>
                <button 
                  onClick={() => addProductToOrder(p)} 
                  className="text-white hover:text-green-400 text-lg transition-colors px-2"
                >
                  +
                </button>
              </div>
            ))}
          </div>
        </div>

        {/* Detalles de la Orden */}
        <div className="lg:col-span-2">
          <form onSubmit={handleSubmit} className="space-y-8 bg-gray-800 border border-gray-700 p-8">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
              <div>
                <label className={labelClass}>ID Transacción</label>
                <input type="text" readOnly value={orderCode} className={`${inputClass} bg-gray-950 text-gray-500 border-dashed`} />
              </div>
              <div>
                <label className={labelClass}>Correo del Cliente</label>
                <input type="email" required value={email} onChange={e => setEmail(e.target.value)} placeholder="CLIENTE@SISTEMA.COM" className={inputClass} />
              </div>
            </div>

            <div className="mt-10">
              <h3 className="text-sm uppercase tracking-widest mb-4 text-white border-b border-gray-700 pb-2">Carrito de Items</h3>
              {selectedProducts.length === 0 ? (
                <div className="py-10 text-center border border-dashed border-gray-700">
                  <p className="text-[10px] text-gray-500 uppercase tracking-widest">No hay productos seleccionados</p>
                </div>
              ) : (
                <div className="space-y-2">
                  {selectedProducts.map((p, index) => (
                    <div key={p.codigoProducto} className="flex justify-between items-center p-3 border border-gray-700 bg-gray-900">
                      <div className="flex-1">
                        <p className="text-xs uppercase tracking-tight">{p.nombreProducto}</p>
                        <p className="text-[9px] text-gray-500 uppercase">PRECIO UNITARIO: ${p.precio}</p>
                      </div>
                      <div className="flex items-center space-x-6">
                        <div className="flex items-center space-x-2">
                          <span className="text-[9px] text-gray-500 uppercase">CANT:</span>
                          <input 
                            type="number" 
                            value={p.cantidad} 
                            onChange={e => updateQuantity(index, parseInt(e.target.value))} 
                            className="w-16 bg-gray-800 border border-gray-600 text-white text-xs p-1 text-center focus:border-white transition-colors" 
                            min="1" 
                          />
                        </div>
                        <button type="button" onClick={() => removeProduct(index)} className="text-gray-500 hover:text-red-500 text-[9px] uppercase transition-colors">
                          [ Remover ]
                        </button>
                      </div>
                    </div>
                  ))}
                  <div className="flex justify-end pt-6 mt-4">
                    <div className="text-right border-t-2 border-white pt-4 px-4">
                      <p className="text-[10px] text-gray-500 uppercase mb-1">Total a Pagar</p>
                      <p className="text-3xl text-white tracking-tighter font-medium">${calculateTotal().toFixed(2)}</p>
                    </div>
                  </div>
                </div>
              )}
            </div>

            <div className="flex justify-end space-x-8 pt-6">
              <button type="button" onClick={() => navigate('/productos')} className="text-gray-500 hover:text-white uppercase text-xs tracking-widest transition-colors">
                [ Volver ]
              </button>
              <button 
                type="submit" 
                disabled={selectedProducts.length === 0} 
                className="bg-white text-black px-10 py-3 hover:bg-gray-200 transition-colors uppercase text-xs tracking-widest font-medium disabled:bg-gray-700 disabled:text-gray-500"
              >
                Confirmar Orden
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}
