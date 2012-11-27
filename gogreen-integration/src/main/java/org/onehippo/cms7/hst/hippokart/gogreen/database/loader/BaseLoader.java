package org.onehippo.cms7.hst.hippokart.gogreen.database.loader;

import au.com.bytecode.opencsv.CSVReader;
import com.konakartadmin.bl.AdminMgrFactory;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Transaction;
import org.onehippo.cms7.hst.hippokart.gogreen.SynchronizeGoGreenData;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class BaseLoader {

    protected final AdminMgrFactory adminMgrFactory;
    protected final String filePath;

    protected Connection connection;

    public BaseLoader(final AdminMgrFactory adminMgrFactory, final String filePath) throws Exception {

        this.adminMgrFactory = adminMgrFactory;
        this.filePath = filePath;

        System.out.println("\nProcessing : " + filePath);
    }


    public void process() throws Exception {
        try {
            begin();
            internalProcess();
            commit();
        } catch (Exception e) {
            e.printStackTrace();
            roolback();
        } finally {
           close();
        }
    }
    
    protected void internalProcess() throws Exception  {
        final URL csvFile = SynchronizeGoGreenData.class.getClassLoader().getResource(filePath);

        if (csvFile != null) {
            final InputStream csvIS = csvFile.openStream();
            final InputStreamReader csvISR = createInputStream(csvIS);

            String[] csvLine;
            final CSVReader csvReader = new CSVReader(csvISR, ',', '"', '~', 1);

            int i=0;

            while ((csvLine = csvReader.readNext()) != null) {
                System.out.println("process the line: " + i);
                processRow(csvLine);
                i++;
            }
        }
    }

    protected InputStreamReader createInputStream(InputStream csvIS) throws UnsupportedEncodingException {
        return new InputStreamReader(csvIS, "UTF-8");
    }

    protected void begin() throws TorqueException, SQLException {
        if (connection == null || connection.isClosed()) {
            connection = Transaction.begin();
        }
    }

    protected void commit() throws TorqueException {
        if (connection != null) {
            Transaction.commit(connection);
        }
    }

    protected void roolback() {
        if (connection != null) {
            Transaction.safeRollback(connection);
        }
    }
    
    protected void close() throws TorqueException, SQLException {
        if (connection == null && !connection.isClosed()) {
            try {
                connection.close();
            } catch(Exception ex) {
                System.out.println("Problem closing the connection : " + ex.getMessage());
            }
        }
    }

    protected abstract void processRow(String[] csvLine) throws Exception;

}
