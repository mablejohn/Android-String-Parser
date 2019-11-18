package org.rsp.parser.sheet

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow.Builder
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.api.services.sheets.v4.model.ValueRange
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStreamReader
import java.security.GeneralSecurityException

// https://developers.google.com/sheets/api/quickstart/java
object SheetsQuickstart {

    private const val APPLICATION_NAME = "Google Sheets API Java Quickstart"
    private const val CREDENTIALS_FILE_PATH = "/credentials.json"
    private val JSON_FACTORY: JsonFactory? = JacksonFactory.getDefaultInstance()
    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private val SCOPES = listOf(
            SheetsScopes.DRIVE_FILE,
            SheetsScopes.SPREADSHEETS
    )
    private const val TOKENS_DIRECTORY_PATH = "tokens"
    /**
     * Prints the names and majors of students in a sample spreadsheet:
     * https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
     */
    @Throws(IOException::class, GeneralSecurityException::class)
    @JvmStatic
    fun authorizeApiClient(): Sheets {

        val httpTransport: NetHttpTransport = GoogleNetHttpTransport.newTrustedTransport()
        val service: Sheets = Sheets.Builder(
                httpTransport,
                JSON_FACTORY,
                getCredentials(httpTransport)
        ).setApplicationName(APPLICATION_NAME)
                .build()

        val spreadsheetId = "1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms"
        val range = "Class Data!A2:E"
        val response: ValueRange = service.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute()
        val values: List<List<Any>>? = response.getValues()
        if (values == null || values.isEmpty()) {
            println("No data found.")
        } else {
            println("Name, Major")
            for (row in values) {
                // Print columns A and E, which correspond to indices 0 and 4.
                System.out.printf("%s, %s\n", row[0], row[4])
            }
        }
        return service
    }

    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    @Throws(IOException::class)
    private fun getCredentials(HTTP_TRANSPORT: NetHttpTransport): Credential? {
        // Load client secrets.
        val stream = SheetsQuickstart::class.java.getResourceAsStream(CREDENTIALS_FILE_PATH)
                ?: throw FileNotFoundException("Resource not found: $CREDENTIALS_FILE_PATH")
        val clientSecrets: GoogleClientSecrets? = GoogleClientSecrets.load(JSON_FACTORY, InputStreamReader(stream))

        // Build flow and trigger user authorization request.
        val flow: GoogleAuthorizationCodeFlow? = Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(FileDataStoreFactory(File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build()
        val receiver: LocalServerReceiver? = LocalServerReceiver.Builder().setPort(8888).build()
        return AuthorizationCodeInstalledApp(flow, receiver).authorize("user")
    }
}