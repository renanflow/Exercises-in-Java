package br.com.fiap.persistencia.scj16.martins.robson.dao;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

/**
 * Classe base abstrata para Acesso a Dados (DAO) do Gerenciador de Biblioteca
 * @author FIAP/16SCJ - Robson Martins (RM:42055)
 * @param <T> Classe de especializacao do DAO
 */
public abstract class GenericDAO <T> {

	/** Indica se as informacoes de debug serao impressas em stdout. */
	public static final boolean debugInfo = false;
	
	static EntityManagerFactory factory = null;
	private Class<T> classe;

	static {
		factory = Persistence.createEntityManagerFactory("Biblioteca");
	}

	@SuppressWarnings("unchecked")
	public GenericDAO(){

		Class<?> thisClass = getClass();
		if (debugInfo) {
			System.out.println(thisClass);
		}
		ParameterizedType t =
			(ParameterizedType) thisClass.getGenericSuperclass();
		Type t2 = t.getActualTypeArguments()[0];
		if (debugInfo) {
			System.out.println(t2);
		}
		this.classe = (Class<T>) t2;
	}

	/**
	 * Localiza um objeto persistido pelo id
	 * @param id Id do objeto
	 * @return Objeto persistido
	 */
	public T localizar(int id) {

		EntityManager em = factory.createEntityManager();
		EntityTransaction t = em.getTransaction();
		T obj = null;

		try {

			t.begin();
			obj = em.find(classe, id);
			t.commit();

		} catch (Exception e) {

			if (debugInfo) {
				e.printStackTrace();
			}
			if (t.isActive()) t.rollback();

		} finally {

			em.close();
		}

		return obj;
	}

	/**
	 * Lista todos os objetos persistidos da classe
	 * @return Lista de objetos persistidos
	 */
	@SuppressWarnings("unchecked")
	public List<T> listar() {

		EntityManager em = factory.createEntityManager();
		EntityTransaction t = em.getTransaction();
		List<T> list = null;

		try {

			t.begin();
			list = (List<T>) em.createQuery(
					"from " + classe.getSimpleName()).getResultList();
			t.commit();

		} catch (Exception e) {

			if (debugInfo) {
				e.printStackTrace();
			}
			if (t.isActive()) t.rollback();

		} finally {

			em.close();
		}

		return list;
	}

	/**
	 * Insere (persiste) um objeto 
	 * @param obj Objeto a ser persistido
	 * @return True se bem sucedido, false se houve erro.
	 */
	public boolean inserir(T obj) {

		EntityManager em = factory.createEntityManager();
		EntityTransaction t = em.getTransaction();
		boolean result = false;
		
		try {

			t.begin();
			em.persist(obj);
			t.commit();
			result = true;

		} catch (Exception e) {

			if (debugInfo) {
				e.printStackTrace();
			}
			if (t.isActive()) t.rollback();

		} finally {

			em.close();

		}
		
		return result;
	}

	/**
	 * Exclui um objeto persistido
	 * @param id Id do objeto a ser removido
	 * @return True se bem sucedido, false se houve erro.
	 */
	public boolean excluir(int id) {

		EntityManager em = factory.createEntityManager();
		EntityTransaction t = em.getTransaction();
		T obj = null;
		boolean result = false;

		try {

			t.begin();
			obj = em.find(classe, id);
			em.remove(obj);
			t.commit();
			result = true;

		} catch (Exception e) {

			if (debugInfo) {
				e.printStackTrace();
			}
			if (t.isActive()) t.rollback();

		} finally {

			em.close();
		}
		
		return result;
	}
}
