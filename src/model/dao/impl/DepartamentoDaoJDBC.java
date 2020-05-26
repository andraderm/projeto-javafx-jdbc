package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import model.dao.DepartamentoDAO;
import model.entities.Departamento;

public class DepartamentoDaoJDBC implements DepartamentoDAO{

	private Connection conn;
	
	public DepartamentoDaoJDBC(Connection conn) {
		this.conn = conn;
	}
	
	@Override
	public void insert(Departamento dept) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("INSERT INTO departamento (Nome) VALUES (?)", 
					Statement.RETURN_GENERATED_KEYS);
			
			st.setString(1, dept.getNome());
			
			int linhasAfetadas = st.executeUpdate();
			
			if (linhasAfetadas > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if (rs.next()) {
					int id = rs.getInt(1);
					dept.setId(id);
				}
				DB.closeResultSet(rs);
			}
			else {
				throw new DbException("Erro inesperado. Nenhuma linha modificada.");
			}
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}
		
	}

	@Override
	public void update(Departamento dept) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("UPDATE departamento "
					+ "SET Nome = ? WHERE Id = ?");
			
			st.setString(1, dept.getNome());
			st.setInt(2, dept.getId());
			
			st.executeUpdate();
			
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}
		
	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("DELETE FROM departamento WHERE Id = ?");
			
			st.setInt(1, id);
			
			st.executeUpdate();
			
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}
		
	}

	@Override
	public Departamento findById(Integer id) {
		 PreparedStatement st = null;
		 ResultSet rs = null;
		 try {
			 st = conn.prepareStatement("SELECT * FROM departamento WHERE Id = ?");
			 
			 st.setInt(1, id);
			 rs = st.executeQuery();
			 if(rs.next()) {
				 Departamento dept = instantiateDepartamento(rs);
				 return dept; 
			 }
			 return null;
		 } catch (SQLException e) {
			 throw new DbException(e.getMessage());
		 } finally {
			 DB.closeStatement(st);
			 DB.closeResultSet(rs);
		 }
	}

	private Departamento instantiateDepartamento(ResultSet rs) throws SQLException {
		Departamento dept = new Departamento();
		dept.setId(rs.getInt("Id"));
		dept.setNome(rs.getString("Nome"));
		return dept;
	}

	@Override
	public List<Departamento> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("SELECT * FROM departamento ORDER BY Nome");
			
			rs = st.executeQuery();
			
			List<Departamento> list = new ArrayList<>();
			Map<Integer, Departamento> map = new HashMap<>();
			while(rs.next()) {
				Departamento dept = map.get(rs.getInt("Id"));
				
				if (dept == null) {
					dept = instantiateDepartamento(rs);
					map.put(rs.getInt("Id"), dept);
					list.add(dept);
				}
			}
			return list;
			
		} catch(SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

}
