package lk.ijse.jsp.servlet;


import lk.ijse.jsp.db.DBConnection;
import lk.ijse.jsp.dto.CustomerDTO;

import javax.json.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Class.forName;

//http://localhost:8080/pos_one/customer
//http://localhost:8080/pos_one/pages/customer? 404
//http://localhost:8080/customer? 404

//http://localhost:8080/pos_one/pages/customer//
//http:://localhost:8080/pos_one/pages/customer
//http:://localhost:8080/pos_one/pages/customer

@WebServlet(urlPatterns = {"/pages/customer"})
public class CustomerServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            /*<!--when the response received catch it and set it to the table-->*/

            forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/shop", "root", "1234");

            String option = req.getParameter("option");

            switch (option) {
                case "GetAll":
                    PreparedStatement pstm = connection.prepareStatement("select * from customer");
                    ResultSet rst = pstm.executeQuery();
                    resp.addHeader("Access-Control-Allow-Origin", "*");


                    JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
                    while (rst.next()) {
                        String id = rst.getString(1);
                        String name = rst.getString(2);
                        String address = rst.getString(3);
                        String contact = rst.getString(4);

                        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
                        objectBuilder.add("id", id);
                        objectBuilder.add("name", name);
                        objectBuilder.add("address", address);
                        objectBuilder.add("contact", contact);
                        arrayBuilder.add(objectBuilder.build());
                    }
                    resp.setContentType("application/json");
                    resp.getWriter().print(arrayBuilder.build());
                    break;

                case "GetIds":
                    PreparedStatement pstm2 = connection.prepareStatement("SELECT Id FROM customer ORDER BY Id DESC LIMIT 1;");
                    ResultSet rst2 = pstm2.executeQuery();
                    resp.addHeader("Access-Control-Allow-Origin", "*");

                    System.out.println("GetIds"+pstm2);

                    JsonArrayBuilder arrayBuilder2 = Json.createArrayBuilder();
                    while (rst2.next()) {
                        String id = rst2.getString("id");
                        int newCustomerId=Integer.parseInt(id.replace("C0-",""))+1;
                        arrayBuilder2.add(newCustomerId);
                    }
                    resp.setContentType("application/json");
                    resp.getWriter().print(arrayBuilder2.build());

                    break;

            }


        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String cusID = req.getParameter("cusID");
        String cusName = req.getParameter("cusName");
        String cusAddress = req.getParameter("cusAddress");
        String cusSalary = req.getParameter("contact");
        String option = req.getParameter("option");


       /* //Json object Create and read Array PostMan...
        JsonReader reader = Json.createReader(req.getReader());
        JsonArray jsonValues = reader.readArray();
        for (JsonValue jsonValue : jsonValues) {
            JsonObject jsonObject = jsonValue.asJsonObject();
            String cusID1 = jsonObject.getString("cusID");
            String cusName1 = jsonObject.getString("cusName");
            String cusAddress1 = jsonObject.getString("cusAddress");
            String contact1 = jsonObject.getString("contact");
            System.out.println(cusID1+" "+cusName1+" "+cusAddress1+" "+contact1);
        }*/

        try {
            forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/shop", "root", "1234");

          /*  switch (option) {
                case "add":*/
            PreparedStatement pstm = connection.prepareStatement("Insert into customer values(?,?,?,?)");
            resp.addHeader("Access-Control-Allow-Origin", "*");

            pstm.setObject(1, cusID);
            pstm.setObject(2, cusName);
            pstm.setObject(3, cusAddress);
            pstm.setObject(4, cusSalary);

            if (pstm.executeUpdate() > 0) {
                /* resp.getWriter().println("Customer Added..!");*/
                JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
                objectBuilder.add("status", "success");
                objectBuilder.add("message", "Customer Added..!");
                resp.setContentType("application/json");
                resp.getWriter().print(objectBuilder.build());


            } else {
                resp.getWriter().println("Customer Not Added..!");
                String jsonResponse = "{\"status\": \"fail\", \"message\": \"Customer Not Added..!\"}";
                resp.getWriter().println(jsonResponse);
            }
            /*  break;*/
              /*  case "delete":
                    PreparedStatement pstm2 = connection.prepareStatement("delete from Customer where id=?");
                    pstm2.setObject(1, cusID);
                    if (pstm2.executeUpdate() > 0) {
                        resp.getWriter().println("Customer Deleted..!");
                    }
                    break;*/
             /*   case "update":
                    PreparedStatement pstm3 = connection.prepareStatement("update Customer set name=?,address=? where id=?");
                    pstm3.setObject(3, cusID);
                    pstm3.setObject(1, cusName);
                    pstm3.setObject(2, cusAddress);
                    if (pstm3.executeUpdate() > 0) {
                        resp.getWriter().println("Customer Updated..!");
                    }
                    break;*/
            /*      }*/


        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
            objectBuilder.add("status", "success");
            objectBuilder.add("message", e.getMessage());
            objectBuilder.add("data", e.getErrorCode());
            resp.setContentType("application/json");
            resp.setStatus(400);
            resp.getWriter().print(objectBuilder.build());

        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /*String cusID = req.getParameter("cusID");
        String cusName = req.getParameter("cusName");
        String cusAddress = req.getParameter("cusAddress");
        String contact = req.getParameter("contact");
        String option = req.getParameter("option");

        System.out.println(cusID+" "+cusName);*/
        resp.addHeader("Access-Control-Allow-Origin", "*");
        JsonReader reader = Json.createReader(req.getReader());
        JsonObject jsonObject = reader.readObject();

        String cusID = jsonObject.getString("cusID");
        String cusName = jsonObject.getString("cusName");
        String cusAddress = jsonObject.getString("cusAddress");
        String contact = jsonObject.getString("contact");
        System.out.println(cusID + " " + cusName + " " + cusAddress + " " + contact);

        try {
            forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/shop", "root", "1234");


            PreparedStatement pstm3 = connection.prepareStatement("update customer set name=?, address=?, contact=? where id=?");

            pstm3.setObject(1, cusName);
            pstm3.setObject(2, cusAddress);
            pstm3.setObject(3, contact);
            pstm3.setObject(4, cusID);
            System.out.println("sql" + pstm3);
            if (pstm3.executeUpdate() > 0) {
                JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
                objectBuilder.add("status", "success");
                objectBuilder.add("message", "Customer Updated..!");
                resp.setContentType("application/json");
                resp.getWriter().print(objectBuilder.build());

            } else {
                JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
                objectBuilder.add("status", "fail");
                objectBuilder.add("message", "Customer Not Updated..!");
                resp.setContentType("application/json");
                resp.setStatus(400);
                resp.getWriter().print(objectBuilder.build());
            }

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
            objectBuilder.add("status", "success");
            objectBuilder.add("message", e.getMessage());
            objectBuilder.add("data", e.getErrorCode());
            resp.setContentType("application/json");
            resp.setStatus(400);
            resp.getWriter().print(objectBuilder.build());


        }
    }

   /* } catch (ClassNotFoundException ex) {
             throw new RuntimeException(ex);
         } catch (SQLException e) {
             JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
             objectBuilder.add("status", "error"); // Changed status to "error"
             objectBuilder.add("message", e.getMessage());
             objectBuilder.add("data", e.getErrorCode());
             resp.setContentType("application/json");
             resp.setStatus(400);
             resp.getWriter().print(objectBuilder.build());
             e.printStackTrace(); // Print stack trace for debugging
         }
*/

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String cusID = req.getParameter("cusID");
        String option = req.getParameter("option");

        try {
            forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/shop", "root", "1234");


            PreparedStatement pstm2 = connection.prepareStatement("delete from customer where id=?");
            resp.addHeader("Access-Control-Allow-Origin", "*");
            pstm2.setObject(1, cusID);

            System.out.println("Sql" + pstm2);
            if (pstm2.executeUpdate() > 0) {
                JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
                objectBuilder.add("status", "success");
                objectBuilder.add("message", "Customer Deleted..!");
                resp.setContentType("application/json");
                resp.getWriter().print(objectBuilder.build());
            } else {
                JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
                objectBuilder.add("status", "fail");
                objectBuilder.add("message", "Customer Not Deleted..!");
                resp.setContentType("application/json");
                resp.setStatus(400);
                resp.getWriter().print(objectBuilder.build());
            }

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
            objectBuilder.add("status", "success");
            objectBuilder.add("message", e.getMessage());
            objectBuilder.add("data", e.getErrorCode());
            resp.setContentType("application/json");
            resp.setStatus(400);
            resp.getWriter().print(objectBuilder.build());

        }

    }


    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.addHeader("Access-Control-Allow-Origin", "*");
        resp.addHeader("Access-Control-Allow-Headers", "Content-Type");
        resp.addHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE");
    }
}
