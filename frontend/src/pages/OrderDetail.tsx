import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import api from '../api/axiosConfig';

interface OrderProduct {
  codigoProducto: string;
  nombreProducto: string;
  precio: number;
  cantidad: number;
}

interface Orden {
  id: string;
  codigoOrden: string;
  fecha: string;
  total: number;
  status: string;
  usuarioCorreo: string;
  productos: OrderProduct[];
}

export default function OrderDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [order, setOrder] = useState<Orden | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchOrderDetail();
  }, [id]);

  const fetchOrderDetail = async () => {
    try {
      const response = await api.get(`/ordenes/${id}`);
      setOrder(response.data.data);
    } catch (error) {
      console.error("Error al obtener detalle de la orden:", error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) return (
    <div className="flex justify-center items-center h-64">
      <div className="text-xl animate-pulse tracking-widest uppercase">Consultando Transacción...</div>
    </div>
  );

  if (!order) return (
    <div className="text-center py-20 border-2 border-dashed border-red-900">
      <p className="text-red-500 uppercase tracking-widest">Orden no encontrada</p>
      <button onClick={() => navigate('/ordenes')} className="mt-4 text-white uppercase text-xs border-b border-white hover:border-gray-500 transition-all">[ Volver al Listado ]</button>
    </div>
  );

  return (
    <div className="max-w-4xl mx-auto">
      <div className="flex justify-between items-center mb-10">
        <h1 className="text-2xl uppercase tracking-widest text-white border-l-4 border-white pl-4">Detalle de Transacción</h1>
        <button onClick={() => navigate('/ordenes')} className="text-gray-500 hover:text-white uppercase text-xs tracking-widest transition-colors">
          [ Regresar ]
        </button>
      </div>

      <div className="bg-gray-800 border border-gray-700 p-8 mb-8 relative overflow-hidden">
        <div className="absolute top-0 right-0 p-4">
          <span className={`px-4 py-1 text-xs uppercase border-2 ${
            order.status === 'COMPLETADA' ? 'border-green-500 text-green-500' : 
            order.status === 'FALLIDA' ? 'border-red-500 text-red-500' : 'border-gray-500 text-gray-500'
          }`}>
            {order.status}
          </span>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-8 mb-10">
          <div>
            <p className="text-[10px] uppercase tracking-widest text-gray-500 mb-1">ID del Sistema</p>
            <p className="text-lg text-white uppercase">{order.codigoOrden}</p>
          </div>
          <div>
            <p className="text-[10px] uppercase tracking-widest text-gray-500 mb-1">Fecha Registro</p>
            <p className="text-sm text-gray-300">{new Date(order.fecha).toLocaleString()}</p>
          </div>
          <div>
            <p className="text-[10px] uppercase tracking-widest text-gray-500 mb-1">Correo Cliente</p>
            <p className="text-sm text-gray-300 uppercase">{order.usuarioCorreo}</p>
          </div>
        </div>

        <div className="border-t border-gray-700 pt-8">
          <h2 className="text-xs uppercase tracking-widest text-gray-400 mb-6">Items Adquiridos</h2>
          <div className="space-y-3">
            {order.productos.map((item, idx) => (
              <div key={idx} className="flex justify-between items-center p-3 border border-gray-700 bg-gray-900/50">
                <div>
                  <p className="text-xs uppercase tracking-tight text-white">{item.nombreProducto}</p>
                  <p className="text-[9px] text-gray-500 uppercase">Unit: ${item.precio.toFixed(2)} | ID: {item.codigoProducto}</p>
                </div>
                <div className="text-right">
                  <p className="text-xs text-gray-300 uppercase">x{item.cantidad}</p>
                  <p className="text-sm text-white font-medium">${(item.precio * item.cantidad).toFixed(2)}</p>
                </div>
              </div>
            ))}
          </div>
        </div>

        <div className="mt-10 pt-6 border-t-2 border-white flex justify-end">
          <div className="text-right">
            <p className="text-[10px] text-gray-500 uppercase mb-1">Total de la Operación</p>
            <p className="text-4xl text-white tracking-tighter">${order.total.toFixed(2)}</p>
          </div>
        </div>
      </div>
    </div>
  );
}
