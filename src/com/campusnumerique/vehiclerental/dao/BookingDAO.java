package com.campusnumerique.vehiclerental.dao;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.List;

import com.campusnumerique.vehiclerental.entity.Booking;
import com.campusnumerique.vehiclerental.entity.Client;
import com.mysql.cj.protocol.Resultset;

public class BookingDAO extends DAO<Booking>{

	@Override
	public boolean create(Booking obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(Booking obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(Booking obj) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean existClientDate(Client client, String startDate, String endDate) throws SQLException{

		String requete = "SELECT * FROM booking WHERE id_client = " + client.getId() + " AND '" + startDate + "'<= endDate AND '" + endDate + "'>= startDate"; 

		ResultSet result = this.connection.createStatement(
		    ResultSet.TYPE_SCROLL_INSENSITIVE, 
		    ResultSet.CONCUR_READ_ONLY
		  ).executeQuery(requete);
		
		if (result.first()) {
			return true;
		} else {
			return false;
		}
	}

	
	public void createBooking(Booking booking) throws SQLException {
		

		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		
		PreparedStatement preparedStatement = null;
		
		String sql = "INSERT INTO booking (id_client, id_car, startDate, endDate, estimatedDistance, estimatedPrice) VALUES (?,?,?,?,?,?)";
		
		preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		

		preparedStatement.setInt(1, booking.getClient().getId());
		preparedStatement.setInt(2, booking.getCar().getId());

		preparedStatement.setDate(3,java.sql.Date.valueOf(dateFormatter.format(booking.getStartDate())));
		preparedStatement.setDate(4, java.sql.Date.valueOf(dateFormatter.format(booking.getEndDate())));
		preparedStatement.setInt(5, booking.getEstimatedDistance());
		preparedStatement.setFloat(6, booking.getEstimatedPrice());
		
		preparedStatement.executeUpdate();
		
		ResultSet rs = preparedStatement.getGeneratedKeys();
		if (rs.first()) {
			booking.setId(rs.getInt(1));
		}
			
	}

	@Override
	public Booking find(int id) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Booking> findAll() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}


}
