package model.dao;

import java.util.List;

import model.entities.Departamento;

public interface DepartamentoDAO {
	
	void insert(Departamento dept);
	void update(Departamento dept);
	void deleteById(Integer id);
	Departamento findById (Integer id);
	List<Departamento> findAll();

}
