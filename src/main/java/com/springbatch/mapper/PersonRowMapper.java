package com.springbatch.mapper;

import com.springbatch.model.Person;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PersonRowMapper implements RowMapper<Person> {

    public static final String ID_COLUMN = "id";
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String CITY = "city";

    public Person mapRow(ResultSet rs, int rowNum) throws SQLException {
        Person person = new Person();

        person.setId(rs.getInt(ID_COLUMN));
        person.setFirstName(rs.getString(FIRST_NAME));
        person.setLastName(rs.getString(LAST_NAME));
        person.setCity(rs.getString(CITY));

        return person;
    }
}
