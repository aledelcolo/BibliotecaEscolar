package biblioteca.dao;

import biblioteca.modelo.EntidadBase;

import java.util.List;

public interface GenericDAO<T extends EntidadBase> {

    void insertar(T entidad) throws DAOException;

    void actualizar(T entidad) throws DAOException;

    void eliminar(int id) throws DAOException;

    T buscarPorId(int id) throws DAOException;

    List<T> listar() throws DAOException;
}
