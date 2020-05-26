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

}
