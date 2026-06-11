import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/axiosConfig';

export default function ProductCreate() {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    nombre: '',
    descripcion: '',
    precio: 0,
    cantidad: 1,
    marca: '',
    proveedor: '',
    categoria: '',
    imagen: 'https://via.placeholder.com/150'
  });

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (formData.cantidad <= 0) {
      alert("El Stock debe ser mayor a cero.");
      return;
    }

    try {
      await api.post('/productos', formData);
      alert("Producto creado con éxito.");
      navigate('/productos');
    } catch (error: any) {
      alert(error.response?.data?.message || "Error al crear el producto.");
    }
  };

  const inputClass = "mt-1 block w-full bg-gray-900 border border-gray-700 focus:border-white focus:ring-0 text-gray-100 p-2 transition-colors placeholder-gray-600 uppercase text-xs";
  const labelClass = "block text-[10px] uppercase tracking-widest text-gray-400 mb-1";

  return (
    <div className="max-w-2xl mx-auto">
      <h1 className="text-2xl uppercase tracking-widest mb-8 border-l-4 border-white pl-4">Nuevo Item Inventario</h1>
      <form onSubmit={handleSubmit} className="space-y-6 bg-gray-800 p-8 border border-gray-700">
        <div>
          <label className={labelClass}>Nombre del Título</label>
          <input type="text" required value={formData.nombre} onChange={e => setFormData({...formData, nombre: e.target.value})} className={inputClass} placeholder="Ej. Super Mario World" />
        </div>
        
        <div className="grid grid-cols-2 gap-6">
          <div>
            <label className={labelClass}>Precio (USD)</label>
            <input type="number" step="0.01" required value={formData.precio} onChange={e => setFormData({...formData, precio: parseFloat(e.target.value)})} className={inputClass} />
          </div>
          <div>
            <label className={labelClass}>Stock Inicial</label>
            <input type="number" required value={formData.cantidad} onChange={e => setFormData({...formData, cantidad: parseInt(e.target.value)})} className={inputClass} />
          </div>
        </div>

        <div>
          <label className={labelClass}>Categoría / Género</label>
          <input type="text" required value={formData.categoria} onChange={e => setFormData({...formData, categoria: e.target.value})} className={inputClass} placeholder="Ej. Plataformas, RPG" />
        </div>

        <div className="grid grid-cols-2 gap-6">
          <div>
            <label className={labelClass}>Marca / Consola</label>
            <input type="text" required value={formData.marca} onChange={e => setFormData({...formData, marca: e.target.value})} className={inputClass} placeholder="Ej. Nintendo, Sega" />
          </div>
          <div>
            <label className={labelClass}>Proveedor</label>
            <input type="text" required value={formData.proveedor} onChange={e => setFormData({...formData, proveedor: e.target.value})} className={inputClass} />
          </div>
        </div>

        <div>
          <label className={labelClass}>URL de Imagen de Portada</label>
          <input type="text" required value={formData.imagen} onChange={e => setFormData({...formData, imagen: e.target.value})} className={inputClass} />
        </div>

        <div>
          <label className={labelClass}>Descripción del Producto</label>
          <textarea required value={formData.descripcion} onChange={e => setFormData({...formData, descripcion: e.target.value})} className={inputClass} rows={3}></textarea>
        </div>

        <div className="flex justify-end space-x-6 pt-4">
          <button type="button" onClick={() => navigate('/productos')} className="text-gray-500 hover:text-white uppercase text-xs tracking-widest transition-colors">
            [ Cancelar ]
          </button>
          <button type="submit" className="bg-white text-black px-8 py-2 hover:bg-gray-200 transition-colors uppercase text-xs tracking-widest font-medium">
            Registrar Item
          </button>
        </div>
      </form>
    </div>
  );
}
