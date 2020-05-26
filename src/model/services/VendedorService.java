package model.services;

import java.util.List;

import model.dao.VendedorDAO;
import model.dao.FabricaDao;
import model.entities.Vendedor;

public class VendedorService {
	
	private VendedorDAO dao = FabricaDao.createVendedorDAO();
	
	public List<Vendedor> findAll(){
		return dao.findAll();
	}
	
	public void saveOrUpdate(Vendedor obj) {
		if (obj.getId() == null) {
			dao.insert(obj);
		} else {
			dao.update(obj);
		}				
	}
	
	public void remove(Vendedor obj) {
		dao.deleteById(obj.getId());
	}

}
