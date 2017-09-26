package com.tony.jpa.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import com.tony.jpa.domain.Employee;
import com.tony.jpa.domain.Department;

public class JpaTest {

	private EntityManager manager;

	public JpaTest(EntityManager manager) {
		this.manager = manager;
	}
	/**
	 * @param args
	 */
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		EntityManagerFactory factory = Persistence.createEntityManagerFactory("persistenceUnit");
		EntityManager manager = factory.createEntityManager();
		JpaTest test = new JpaTest(manager);

		Department department = null;
		
		EntityTransaction tx = manager.getTransaction();
		tx.begin();
		try {
			department = test.createEmployees();
		} catch (Exception e) {
			e.printStackTrace();
		}
		tx.commit();

		//department is now detached
		department.getEmployees().get(0).setName("ANTHONY"); // update one
		department.getEmployees().remove(1); // delete other
		
		System.out.println("Employees size detached: "+ department.getEmployees().size());

		tx.begin();
		try {

			test.merge(department); // try to merge graph
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		tx.commit();
		
		test.listEmployees();

		System.out.println(".. done");
	}




	private void merge(Department department) {
		manager.merge(department);
	}

	private Department createEmployees() {
		int numOfEmployees = manager.createQuery("Select a From Employee a", Employee.class).getResultList().size();
		if (numOfEmployees == 0) {
			// new no managed entity
			Department department = new Department("java");
			
			manager.persist(department);
			
			// entity is now attached
			department.getEmployees().add(new Employee("Jakab Gipsz", department));
			department.getEmployees().add(new Employee("Captain Nemo", department));
			
			return department;
		}
		
		return null;
	}


	private void listEmployees() {
		List<Employee> resultList = manager.createQuery("Select a From Employee a", Employee.class).getResultList();
		System.out.println("num of employess:" + resultList.size());
		for (Employee next : resultList) {
			System.out.println("next employee: " + next);
		}
	}


}
