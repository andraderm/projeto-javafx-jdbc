package model.services;

import java.util.List;

import model.dao.DepartamentoDAO;
import model.dao.FabricaDao;
import model.entities.Departamento;

public class DepartamentoService {
	
	private DepartamentoDAO dao = FabricaDao.createDepartamentoDAO();
	
	public List<Departamento> findAll(){
		return dao.findAll();
	}
	
	public void saveOrUpdate(Departamento obj) {
		if (obj.getId() == null) {
			dao.insert(obj);
		} else {
			dao.update(obj);
		}				
	}
	
	public void remove(Departamento obj) {
		dao.deleteById(obj.getId());
	}

}
