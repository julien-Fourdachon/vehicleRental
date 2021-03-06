package com.campusnumerique.vehicle.servlet.homePage;

import java.io.IOException;
import java.text.SimpleDateFormat;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.Date;
import java.util.HashMap;

import java.util.Map;

import com.campusnumerique.vehiclerental.entity.Client;

import com.campusnumerique.vehiclerental.dao.ClientDAO;

import com.campusnumerique.vehiclerental.dao.CarDAO;
import com.campusnumerique.vehiclerental.entity.Booking;
import com.campusnumerique.vehiclerental.dao.BookingDAO;


public class HomePageServlet extends HttpServlet {
	/**
	 * @throws ClassNotFoundException
	 * @see HttpServlet#HttpServlet()
	 */
	private static final long serialVersionUID = 1L;
	private ClientDAO clientDAO = null;
	private CarDAO carDAO = null;
	private BookingDAO bookingDAO = null;
	public static final String VUE = "/pages/homePage.jsp";
	public static final String VUEPOST = "/pages/booking.jsp";

	public static final String CHAMP_FIRSTNAME = "firstName";
	public static final String CHAMP_LASTNAME = "lastName";
	public static final String CHAMP_STARTDATE = "startDate";
	public static final String CHAMP_ENDDATE = "endDate";
	public static final String CHAMP_ESTIMATEDDISTANCE = "estimatedDistance";
	public static final String CHAMP_IDCLIENT = "id_client";
	public static final String ATT_ERREURS = "erreurs";
	public static final String ATT_RESULTAT = "resultat";

	public HomePageServlet() throws ClassNotFoundException {
		super();
		clientDAO = new ClientDAO();
		carDAO = new CarDAO();
		bookingDAO = new BookingDAO();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/* Affichage de la page d'inscription */

		this.getServletContext().getRequestDispatcher(VUE).forward(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		boolean isValidatedDate = true;

		/* Affichage de la vue booking au post du formulaire */

		/*
		 * RequestDispatcher rd = request.getRequestDispatcher("/booking");
		 * rd.forward(request,response);
		 */
		/* Récupérations des champs du formulaire */

		String resultat;
		Map<String, String> erreurs = new HashMap<String, String>();

		String firstName = request.getParameter(CHAMP_FIRSTNAME);
		String lastName = request.getParameter(CHAMP_LASTNAME);
		String startDate = request.getParameter(CHAMP_STARTDATE);
		String endDate = request.getParameter(CHAMP_ENDDATE);
		String estimatedDistance = request.getParameter(CHAMP_ESTIMATEDDISTANCE);

		try {
			validationFirstName(firstName);
		} catch (Exception e) {
			erreurs.put(CHAMP_FIRSTNAME, e.getMessage());
		}
		try {
			validationLastName(lastName);
		} catch (Exception e) {
			erreurs.put(CHAMP_LASTNAME, e.getMessage());
		}
		try {
			validationStartDate(startDate);
		} catch (Exception e) {
			erreurs.put(CHAMP_STARTDATE, e.getMessage());
			isValidatedDate = false;
		}
		try {
			validationEndDate(endDate);
		} catch (Exception e) {
			erreurs.put(CHAMP_ENDDATE, e.getMessage());
			isValidatedDate = false;
		}

		if (isValidatedDate) {
			try {
				validationEndDate2(endDate, startDate);
			} catch (Exception e) {
				erreurs.put(CHAMP_ENDDATE, e.getMessage());
			}
		}

		try {
			validationEstimatedDistance(estimatedDistance);
		} catch (Exception e) {
			erreurs.put(CHAMP_ESTIMATEDDISTANCE, e.getMessage());
		}

		/* Initialisation du résultat global de la validation. */
		if (erreurs.isEmpty()) {
			resultat = "Succès de l'inscription.";
		} else {
			resultat = "Échec de l'inscription.";
		}

		/* Stockage du résultat et des messages d'erreur dans l'objet request */
		request.setAttribute(ATT_ERREURS, erreurs);
		request.setAttribute(ATT_RESULTAT, resultat);

		/* Gérer les erreurs de validation ici */

		Client client = null;
		try {
			client = clientDAO.findName(firstName, lastName);
		} catch (Exception e) {
			erreurs.put(CHAMP_FIRSTNAME, "Client inexistant: merci de vous inscrire");
		}
		;

		if (client != null) {

			if (!client.ableToBook())
				erreurs.put(CHAMP_FIRSTNAME, "Erreur : client pas apte à conduire");

			boolean bookingClientDateExist = false;
			try {
				bookingClientDateExist = bookingDAO.existClientDate(client, startDate, endDate);
			} catch (Exception e) {

			}

			if (bookingClientDateExist)
				erreurs.put(CHAMP_FIRSTNAME, "Erreur : déjà une réservation pour ce client à cette date");
	
		}
		if (erreurs.isEmpty()) {
			SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
			Date dateStartDate = null;
			try {
				dateStartDate = dateFormatter.parse(startDate);
			} catch (java.text.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Date dateEndDate = null;
			try {
				dateEndDate = dateFormatter.parse(endDate);
			} catch (java.text.ParseException e) {
				e.printStackTrace();
			}
			
			Booking booking = new Booking(client, dateStartDate, dateEndDate, Integer.parseInt(estimatedDistance));
		
			
			HttpSession session = request.getSession(false);
			session.setAttribute("booking", booking);


			RequestDispatcher rd = request.getRequestDispatcher("/booking");
			
			request.setAttribute("idClient", String.valueOf(client.getId()));
			request.setAttribute("startDate", startDate);
			request.setAttribute("endDate", endDate);
			request.setAttribute("estimatedDistance", estimatedDistance);
			request.setAttribute("action", "get");
			rd.forward(request, response);
		} else {
			this.getServletContext().getRequestDispatcher(VUE).forward(request, response);
		}

	}

	private void validationFirstName(String firstName) throws Exception {
		if (firstName.trim().length() == 0) {
			throw new Exception("merci de renseigner votre prénom");
		}
	}

	private void validationLastName(String lastName) throws Exception {
		if (lastName.trim().length() == 0) {
			throw new Exception("merci de renseigner votre nom");
		}
	}

	private void validationStartDate(String startDate) throws Exception {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date currenDate = new Date();
		Date dateStart = null;
		try {
			dateStart = formatter.parse(startDate);
		} catch (Exception e) {
			throw new Exception("merci de renseigner une date valide");
		}
		int res = dateStart.compareTo(currenDate);
		if (res < 0) {
			throw new Exception("La date de début de réservation ne peut ");

		}

	}

	private void validationEndDate(String endDate) throws Exception {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date date1 = null;
		try {
			date1 = formatter.parse(endDate);
		} catch (Exception e) {
			throw new Exception("merci de renseigner une date valide");
		}

	}

	private void validationEndDate2(String endDate, String startDate) throws Exception {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date date1 = formatter.parse(startDate);
		Date date2 = null;

		try {
			date2 = formatter.parse(endDate);
		} catch (Exception e) {
			throw new Exception("merci de renseigner une date valide");
		}
		int res = date1.compareTo(date2);

		if (res > 0) {
			throw new Exception("La end Date ne peut être inférieure à la start Date, merci de changer votre choix");
		}

	}

	private void validationEstimatedDistance(String estimatedDistance) throws Exception {
		long distanceEstimated = 0;

		if (estimatedDistance.equals("")){
			throw new Exception("veuillez remplir ce champ");
		}
		
		try{
		 distanceEstimated = Long.parseLong(estimatedDistance);
		}catch (NumberFormatException e) {
			throw new Exception("merci de rentrer uniquement des chiffres");
		}
		if(distanceEstimated <= 0 || distanceEstimated > 9999){
			throw new Exception("la distance doit être comprise entre 1 et 9999");
		}
	}

}