import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import api from '../api/axiosConfig';

interface Orden {
  id: string;
  codigoOrden: string;
  fecha: string;
  total: number;
  status: string;
  usuarioCorreo: string;
}

export default function OrderList() {
  const [orders, setOrders] = useState<Orden[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchOrders();
  }, []);

  const fetchOrders = async () => {
    try {
      const response = await api.get('/ordenes');
      setOrders(response.data.data);
    } catch (error) {
      console.error("Error al obtener órdenes:", error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) return (
    <div className="flex justify-center items-center h-64">
      <div className="text-xl animate-pulse tracking-widest uppercase">Cargando Historial...</div>
    </div>
  );

  return (
    <div>
      <h1 className="text-3xl uppercase tracking-widest text-white border-l-4 border-white pl-4 mb-10">Historial de Transacciones</h1>

      <div className="bg-gray-800 border border-gray-700 overflow-hidden">
        <table className="min-w-full divide-y divide-gray-700">
          <thead className="bg-gray-900">
            <tr>
              <th className="px-6 py-3 text-left text-[10px] uppercase tracking-widest text-gray-400">ID Orden</th>
              <th className="px-6 py-3 text-left text-[10px] uppercase tracking-widest text-gray-400">Fecha</th>
              <th className="px-6 py-3 text-left text-[10px] uppercase tracking-widest text-gray-400">Cliente</th>
              <th className="px-6 py-3 text-left text-[10px] uppercase tracking-widest text-gray-400">Total</th>
              <th className="px-6 py-3 text-left text-[10px] uppercase tracking-widest text-gray-400">Status</th>
              <th className="px-6 py-3 text-right text-[10px] uppercase tracking-widest text-gray-400">Accion</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-700">
            {orders.map((order) => (
              <tr key={order.id} className="hover:bg-gray-750 transition-colors group">
                <td className="px-6 py-4 whitespace-nowrap text-xs uppercase tracking-tighter text-white">{order.codigoOrden}</td>
                <td className="px-6 py-4 whitespace-nowrap text-[10px] text-gray-400">{new Date(order.fecha).toLocaleString()}</td>
                <td className="px-6 py-4 whitespace-nowrap text-[10px] text-gray-300 uppercase">{order.usuarioCorreo}</td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-white font-medium">${order.total.toFixed(2)}</td>
                <td className="px-6 py-4 whitespace-nowrap">
                  <span className={`px-2 py-0.5 text-[9px] uppercase border ${
                    order.status === 'COMPLETADA' ? 'border-green-500 text-green-500' : 
                    order.status === 'FALLIDA' ? 'border-red-500 text-red-500' : 'border-gray-500 text-gray-500'
                  }`}>
                    {order.status}
                  </span>
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-right text-[10px] uppercase">
                  <Link to={`/ordenes/${order.id}`} className="text-white hover:border-b border-white transition-all">
                    [ Ver Detalle ]
                  </Link>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {orders.length === 0 && (
        <div className="text-center py-20 border-2 border-dashed border-gray-800 mt-4">
          <p className="text-gray-500 uppercase tracking-widest">No se han registrado órdenes aún</p>
        </div>
      )}
    </div>
  );
}
