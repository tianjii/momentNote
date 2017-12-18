package com.sangs.momentNote;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;


public class dbDriver {

	public void dbDriver() {
        //Empty default constructor
	}

	public Connection connect() {
		// SQLite connection string
        String url = "jdbc:sqlite:platypus.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public void createNewDatabase(String fileName) {

        String url = "jdbc:sqlite:"+fileName;

        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

	public void createTables() {
        // SQL statement for creating a new table
        String sql_note = "CREATE TABLE IF NOT EXISTS Notes(\n"
                + "	note_id INTEGER PRIMARY KEY ASC,\n"
                + "	title text NOT NULL,\n"
                + " content text NOT NULL\n"
                //+ " CONSTRAINT no_repeat_id unique(note_id),\n"
                //+ " CONSTRAINT no_repeat_note unique(title)\n"
                + ");";
        String sql_tag = "CREATE TABLE IF NOT EXISTS Tags(\n"
                + "	tag_id INTEGER PRIMARY KEY,\n"
                + "	title text NOT NULL\n"
                //+ " CONSTRAINT no_repeat_tag unique(title),\n"
                //+ " CONSTRAINT no_repeat_id unique(tag_id)\n"
                + ");";
        String sql_notetag = "CREATE TABLE IF NOT EXISTS NoteTag(\n"
                + "	note text NOT NULL,\n"
                + "	tag text NOT NULL\n"
                //+ " CONSTRAINT no_repeats unique(note_id, tag_id)\n"
                + ");";

        try (Connection conn = this.connect();
                Statement stmt = conn.createStatement()) {
            // create 3 new tables if they don't exist
            stmt.execute(sql_note);
            System.out.println("Note table has been created");
            stmt.execute(sql_tag);
            System.out.println("Tag table has been created");
            stmt.execute(sql_notetag);
            System.out.println("All required tables have successfully been created");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }


    }

    //Inserts note content into table if it doesn't already exist
	public void insert(String title, String content) {

        String sql = "INSERT INTO Notes(title, content) VALUES(?,?)";

        try (Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, content);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

	public String searchByTitle(String searchTitle) {
		String sql = "SELECT title FROM Notes WHERE title = ?";
		String result = null;

		try (Connection conn = this.connect();
				PreparedStatement pstmt  = conn.prepareStatement(sql)){

    // set the value
   	 		pstmt.setString(1, searchTitle);
    //
   	 		ResultSet rs  = pstmt.executeQuery();

    // loop through the result set

   	 		while (rs.next()) {
   	 			result = rs.getString("title");
   	 		}

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return result;

	}

	public ObservableList<String> selections(String searchquery){

		ObservableSet<String> selectables = FXCollections.observableSet();

		String existTag = searchByTitle(searchquery);
		if (existTag != null) {
			selectables.add(existTag);
		}
		//search by tag
		String sql = "SELECT note FROM NoteTag WHERE tag = ?";

		try (Connection conn = this.connect();
				PreparedStatement pstmt  = conn.prepareStatement(sql)){

    // set the value
   	 		pstmt.setString(1, searchquery);
    //
   	 		ResultSet rs  = pstmt.executeQuery();

    // loop through the result set

   	 		while (rs.next()) {
   	 			selectables.add(rs.getString("note"));
   	 		}

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		ObservableList<String> selectList = FXCollections.observableArrayList(selectables);

        //return observable set of tags
		return selectList;

	}

	//Load content of selected note
	public String loadContent(String searchTitle) {
		String content = "";

		String sql = "SELECT content FROM Notes WHERE title = ?";

        try (Connection conn = this.connect();
       		 PreparedStatement pstmt  = conn.prepareStatement(sql)){

        // set the value
       	 	pstmt.setString(1, searchTitle);
        //
        ResultSet rs  = pstmt.executeQuery();

        // loop through the result set

        	while (rs.next()) {
        		content =  rs.getString("content");
        	}
        } catch (SQLException e) {
        	System.out.println(e.getMessage());
        }

        return content;
	}

    //Replace content in table with newly edited content
	public void editNote(String title, String content) {
		String sql = "UPDATE Notes SET content = ? "
                + "WHERE title = ?";

        try (Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // set the corresponding param
            pstmt.setString(1, content);
            pstmt.setString(2, title);
            // update

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    //Add new tags to table, add corresponding tag-note relationships
	public void addTags(String name, ObservableList<String> tagSet) {
		String sql = "INSERT INTO Tags(title) VALUES(?)";
		String sql2 = "INSERT INTO NoteTag(note, tag) VALUES(?, ?)";
		for (String tag: tagSet)
		{
			try (Connection conn = this.connect();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {
		        pstmt.setString(1, tag);
		        pstmt.executeUpdate();
			} catch (SQLException e) {
		            System.out.println(e.getMessage());
		    }

			try (Connection conn = this.connect();
				PreparedStatement pstmt = conn.prepareStatement(sql2)) {
			    pstmt.setString(1, name);
			    pstmt.setString(2, tag);
			    pstmt.executeUpdate();
			} catch (SQLException e) {
			        System.out.println(e.getMessage());
			}

		}

	}
}


