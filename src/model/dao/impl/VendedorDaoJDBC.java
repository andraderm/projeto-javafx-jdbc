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
import model.dao.VendedorDAO;
import model.entities.Departamento;
import model.entities.Vendedor;

public class VendedorDaoJDBC implements VendedorDAO {
	
	private Connection conn;
	
	public VendedorDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Vendedor vend) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("INSERT INTO vendedor "
					+ "(Nome, Email, DataNascimento, Salario, IdDepartamento) "
					+ "VALUES (?, ?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);
			
			st.setString(1, vend.getNome());
			st.setString(2, vend.getEmail());
			st.setDate(3,  new java.sql.Date(vend.getDataNascimento().getTime()));
			st.setDouble(4,  vend.getSalario());
			st.setInt(5, vend.getDepartamento().getId());
			
			int linhasAfetadas = st.executeUpdate();
			
			if (linhasAfetadas > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if (rs.next()) {
					int id = rs.getInt(1);
					vend.setId(id);
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
	public void update(Vendedor vend) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("UPDATE vendedor "
					+ "SET Nome = ?, Email = ?, DataNascimento = ?, Salario = ?, IdDepartamento = ? "
					+ "WHERE Id = ?");
			
			st.setString(1, vend.getNome());
			st.setString(2, vend.getEmail());
			st.setDate(3,  new java.sql.Date(vend.getDataNascimento().getTime()));
			st.setDouble(4,  vend.getSalario());
			st.setInt(5, vend.getDepartamento().getId());
			st.setInt(6,  vend.getId());
			
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
			st = conn.prepareStatement("DELETE FROM vendedor WHERE Id = ?");
			
			st.setInt(1,  id);
			
			st.executeUpdate();			
			
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}		
	}

	@Override
	public Vendedor findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("SELECT vendedor.*, departamento.Nome as DepNome "
					+ "FROM vendedor INNER JOIN departamento "
					+ "ON vendedor.IdDepartamento = departamento.Id "
					+ "WHERE vendedor.Id = ?");
			
			st.setInt(1, id);
			rs = st.executeQuery();
			if (rs.next()) {
				Departamento dept = instantiateDepartamento(rs);
				Vendedor vend = instantiateVendedor(rs, dept);
				return vend;
			}
			return null;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
		
	}

	private Vendedor instantiateVendedor(ResultSet rs, Departamento dept) throws SQLException {
		Vendedor vend = new Vendedor();
		vend.setId(rs.getInt("Id"));
		vend.setNome(rs.getString("Nome"));
		vend.setEmail(rs.getString("Email"));
		vend.setSalario(rs.getDouble("Salario"));
		vend.setDataNascimento(rs.getDate("DataNascimento"));
		vend.setDepartamento(dept);
		return vend;
	}

	private Departamento instantiateDepartamento(ResultSet rs) throws SQLException {
		Departamento dept = new Departamento();
		dept.setId(rs.getInt("IdDepartamento"));
		dept.setNome(rs.getString("DepNome"));
		return dept;
	}

	@Override
	public List<Vendedor> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("SELECT vendedor.*, departamento.Nome as DepNome "
					+ "FROM vendedor INNER JOIN departamento "
					+ "ON vendedor.IdDepartamento = departamento.Id "
					+ "ORDER BY Nome");
			
			rs = st.executeQuery();
			
			List<Vendedor> list = new ArrayList<>();
			Map<Integer, Departamento> map = new HashMap<>();
			
			while (rs.next()) {
				Departamento dept = map.get(rs.getInt("IdDepartamento"));
				
				if (dept == null) {
					dept = instantiateDepartamento(rs);
					map.put(rs.getInt("IdDepartamento"), dept);
				}
				
				Vendedor vend = instantiateVendedor(rs, dept);
				list.add(vend);
			}
			return list;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	@Override
	public List<Vendedor> findByDepartamento(Departamento departamento) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("SELECT vendedor.*, departamento.Nome as DepNome "
					+ "FROM vendedor INNER JOIN departamento "
					+ "ON vendedor.IdDepartamento = departamento.Id "
					+ "WHERE IdDepartamento = ? "
					+ "ORDER BY Nome");
			
			st.setInt(1, departamento.getId());
			rs = st.executeQuery();
			
			List<Vendedor> list = new ArrayList<>();
			Map<Integer, Departamento> map = new HashMap<>();
			
			while (rs.next()) {
				Departamento dept = map.get(rs.getInt("IdDepartamento"));
				
				if (dept == null) {
					dept = instantiateDepartamento(rs);
					map.put(rs.getInt("IdDepartamento"), dept);
				}
				
				Vendedor vend = instantiateVendedor(rs, dept);
				list.add(vend);
			}
			return list;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

}
