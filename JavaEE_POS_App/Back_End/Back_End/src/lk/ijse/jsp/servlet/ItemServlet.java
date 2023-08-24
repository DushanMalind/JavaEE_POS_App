package lk.ijse.jsp.servlet;

import lk.ijse.jsp.dto.ItemDTO;

import javax.json.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

import static java.lang.Class.forName;

@WebServlet(urlPatterns = "/pages/item")
public class ItemServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/shop", "root", "1234");

            String option = req.getParameter("option");

            switch (option){
                case "getAll":
                    PreparedStatement pstm = connection.prepareStatement("select * from items");
                    ResultSet rst = pstm.executeQuery();
                    resp.addHeader("Access-Control-Allow-Origin", "*");


                    JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
                    while (rst.next()) {
                        String code = rst.getString(1);
                        String description = rst.getString(2);
                        String qty = rst.getString(3);
                        String unitPrice = rst.getString(4);

                        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
                        objectBuilder.add("code", code);
                        objectBuilder.add("description", description);
                        objectBuilder.add("qty", qty);
                        objectBuilder.add("unitPrice", unitPrice);
                        arrayBuilder.add(objectBuilder.build());
                    }
                    resp.setContentType("application/json");
                    resp.getWriter().print(arrayBuilder.build());

                    break;

                case "getNewId":
                    PreparedStatement pstm1 = connection.prepareStatement("select id from items order by id desc limit 1");
                    ResultSet rst1 = pstm1.executeQuery();
                    resp.addHeader("Access-Control-Allow-Origin", "*");

                    JsonArrayBuilder arrayBuilder2 = Json.createArrayBuilder();
                    while (rst1.next()) {
                        String id = rst1.getString("id");
                        int newItemId=Integer.parseInt(id.replace("I0-",""))+1;
                        arrayBuilder2.add(newItemId);
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

        String code = req.getParameter("code");
        String itemName = req.getParameter("description");
        String qty = req.getParameter("qty");
        String unitPrice = req.getParameter("unitPrice");
        String option = req.getParameter("option");
        resp.addHeader("Access-Control-Allow-Origin", "*");

       // System.out.println(code+" "+itemName+" "+qty+" "+unitPrice+" ");
//
        try {
            forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/shop", "root", "1234");

                    PreparedStatement pstm = connection.prepareStatement("INSERT INTO items values(?,?,?,?)");
                    pstm.setObject(1, code);
                    pstm.setObject(2, itemName);
                    pstm.setObject(3, qty);
                    pstm.setObject(4, unitPrice);
                    System.out.println("sql"+pstm);
                    if (pstm.executeUpdate() > 0) {
                        // resp.getWriter().println("Item Added..!");
                        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
                        objectBuilder.add("message", "Item Added..!");
                        resp.setContentType("application/json");
                        resp.getWriter().print(objectBuilder.build());

                    } else {
                        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
                        objectBuilder.add("message", "Item Not Added..!");
                        resp.setContentType("application/json");
                        resp.setStatus(400);
                        resp.getWriter().print(objectBuilder.build());
                    }

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
            objectBuilder.add("error", e.getMessage());
            resp.setContentType("application/json");
            resp.setStatus(400);
            resp.getWriter().print(objectBuilder.build());

        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       /* String code = req.getParameter("code");
        String itemName = req.getParameter("description");
        String qty = req.getParameter("qty");
        String unitPrice = req.getParameter("unitPrice");
        String option = req.getParameter("option");
        */

        JsonReader reader = Json.createReader(req.getReader());
        JsonObject jsonObject = reader.readObject();
        resp.addHeader("Access-Control-Allow-Origin", "*");

        String code = jsonObject.getString("code");
        String itemName = jsonObject.getString("description");
        String qty = jsonObject.getString("qty");
        String unitPrice = jsonObject.getString("unitPrice");

        System.out.println(code+" "+itemName+" "+qty+" "+unitPrice);

        try {
            forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/shop", "root", "1234");

            PreparedStatement pstm = connection.prepareStatement("update items set description=?,qty=?,unitPrice=? where id=?");
            pstm.setObject(1, itemName);
            pstm.setObject(2, qty);
            pstm.setObject(3, unitPrice);
            pstm.setObject(4, code);

            System.out.println("sql"+pstm);

            if (pstm.executeUpdate() > 0) {
                JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
                objectBuilder.add("message", "Item Updated..!");
                resp.setContentType("application/json");
                resp.getWriter().print(objectBuilder.build());
            } else {
                JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
                objectBuilder.add("message", "Item Not Updated..!");
                resp.setContentType("application/json");
                resp.setStatus(400);
                resp.getWriter().print(objectBuilder.build());
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
            objectBuilder.add("error", e.getMessage());
            resp.setContentType("application/json");
            resp.setStatus(400);
            resp.getWriter().print(objectBuilder.build());
        }
    }


    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String code = req.getParameter("code");
        resp.addHeader("Access-Control-Allow-Origin", "*");


        try {
            forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/shop", "root", "1234");

            PreparedStatement pstm = connection.prepareStatement("delete from items where id=?");
            pstm.setObject(1, code);
            if (pstm.executeUpdate() > 0) {
                JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
                objectBuilder.add("message", "Item Deleted..!");
                resp.setContentType("application/json");
                resp.getWriter().print(objectBuilder.build());
            } else {
                JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
                objectBuilder.add("message", "Item Not Deleted..!");
                resp.setContentType("application/json");
                resp.setStatus(400);
                resp.getWriter().print(objectBuilder.build());
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);

        } catch (SQLException e) {
            JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
            objectBuilder.add("error", e.getMessage());
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
