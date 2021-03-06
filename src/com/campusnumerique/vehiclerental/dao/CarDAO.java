package com.campusnumerique.vehiclerental.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;

import com.campusnumerique.vehiclerental.entity.Car;

public class CarDAO extends DAO<Car>{

	@Override
	public boolean create(Car obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(Car obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(Car obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Car find(int id)  throws SQLException {
		Car car = new Car();  
		
		ResultSet result = this.connection.createStatement(
		    ResultSet.TYPE_SCROLL_INSENSITIVE, 
		    ResultSet.CONCUR_READ_ONLY
		  ).executeQuery("SELECT * FROM car WHERE id = " + id);
		if(result.first())
			car = new Car(result.getInt("id"), result.getString("brand"), result.getString("model"), result.getString("plateNumber"), result.getString("color"), result.getFloat("price"), result.getFloat("kilometerRate"), result.getInt("horsePower"));         
		
		return car;
	}

	@Override
	public List<Car> findAll()throws SQLException{
		ArrayList<Car> cars = new ArrayList<Car>();
		ResultSet result = this.connection.createStatement(
		    ResultSet.TYPE_SCROLL_INSENSITIVE, 
		    ResultSet.CONCUR_READ_ONLY
		  ).executeQuery("SELECT * FROM car");
		while(result.next()){
			Car car = new Car(); 
			car = new Car(result.getInt("id"), result.getString("brand"), result.getString("model"), result.getString("plateNumber"), result.getString("color"), result.getFloat("price"), result.getFloat("kilometerRate"), result.getInt("horsePower"));         
			cars.add(car);
		}
		return cars;
	}

	public JSONArray findAllAsJson() {
		JSONArray cars = new JSONArray();
		ResultSet result;
		try {
			result = this.connection.createStatement(
			    ResultSet.TYPE_SCROLL_INSENSITIVE, 
			    ResultSet.CONCUR_READ_ONLY
			  ).executeQuery("SELECT * FROM car");
			while(result.next()){
				Car car = new Car(); 
				car = new Car(result.getInt("id"), result.getString("brand"), result.getString("model"), result.getString("plateNumber"), result.getString("color"), result.getFloat("price"), result.getFloat("kilometerRate"), result.getInt("horsePower"));         
				cars.put(car.getInfos());
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cars;
	}

	public List<Car> availableCar(int age, String startDate, String endDate, String estimatedDistance) throws SQLException{
		ArrayList<Car> cars = new ArrayList<Car>();
		String requete = "";
		
		if (age >= 25) { 
			requete = "SELECT id, brand, model, color, plateNumber, kilometerRate, horsePower, price, price + kilometerRate * " + estimatedDistance + " as bookingPrice FROM car WHERE NOT EXISTS ( SELECT * FROM booking WHERE id_car = car.id AND '" + startDate + "'<= endDate AND '" + endDate + "'>= startDate)"; 
		} else {
			int horsePower=8;
			if (age >= 21) 
				horsePower = 13;
			
			requete = "SELECT id, brand, model, color, plateNumber, kilometerRate, horsePower, price, price + kilometerRate * " + estimatedDistance + " as bookingPrice FROM car WHERE horsePower< " + horsePower + " AND NOT EXISTS ( SELECT * FROM booking WHERE id_car = car.id AND '" + startDate + "'<= endDate AND '" + endDate + "'>= startDate)"; 
		}

		ResultSet result = this.connection.createStatement(
		    ResultSet.TYPE_SCROLL_INSENSITIVE, 
		    ResultSet.CONCUR_READ_ONLY
		  ).executeQuery(requete);
		while(result.next()){
			Car car = new Car(result.getInt("id"), result.getString("brand"), result.getString("model"), result.getString("plateNumber"), result.getString("color"), result.getFloat("price"), result.getFloat("kilometerRate"), result.getInt("horsePower"), result.getFloat("bookingPrice") );         
			cars.add(car);
		}
		return cars;
	}

}
