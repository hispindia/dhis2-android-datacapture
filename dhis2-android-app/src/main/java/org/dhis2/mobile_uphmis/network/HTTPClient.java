/*
 * Copyright (c) 2014, Araz Abishov
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.dhis2.mobile_uphmis.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

import org.dhis2.mobile_uphmis.R;
import org.dhis2.mobile_uphmis.utils.PrefUtils;

import android.content.Context;
import android.util.Log;

public class HTTPClient {
    private static final int CONNECTION_TIME_OUT = 30000;

    private HTTPClient() {
    }


    public static Response get(String server, String creds, String parent_dis) {
        Log.i("GET", server);
        int code = -1;
        String body = "";

        HttpURLConnection connection = null;
        //@Sou user-agent string added
        try {
            URL url = new URL(server);
            connection = (HttpURLConnection) url.openConnection();
            connection.setInstanceFollowRedirects(false);
            connection.setConnectTimeout(CONNECTION_TIME_OUT);
            connection.setRequestProperty("Authorization", "Basic " + creds);
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-agent", "uphmis_aws_3009/" + System.getProperty("http.agent"));
            connection.setRequestProperty("districtuid:", parent_dis);
            connection.setDoInput(true);
            connection.connect();
            code = connection.getResponseCode();
            body = readInputStream(connection.getInputStream());
        } catch (MalformedURLException e) {
            code = HttpURLConnection.HTTP_NOT_FOUND;
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            code = HttpURLConnection.HTTP_NOT_FOUND;
        } catch (IOException one) {
            one.printStackTrace();
            try {
                if (connection != null) {
                    code = connection.getResponseCode();
                }
            } catch (IOException two) {
                two.printStackTrace();
            }
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        Log.i(Integer.toString(code), body);
        Log.d("GET--", body);
        return (new Response(code, body));
    }

    public static Response post(String server, String creds, String data) {
        Log.d("POST--data", data.toString());
        Log.i("POST", server);
        int code = -1;
        String body = "";
        String parentdis = "";
        HttpURLConnection connection = null;
        try {
            URL url = new URL(server);
            connection = (HttpURLConnection) url.openConnection();
            connection.setInstanceFollowRedirects(false);
            connection.setConnectTimeout(CONNECTION_TIME_OUT);
            connection.setRequestProperty("Authorization", "Basic " + creds);
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            connection.setRequestProperty("districtuid:", parentdis);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("User-agent", "uphmis_aws_3009/" + System.getProperty("http.agent"));

            connection.setDoOutput(true);
            OutputStream output = connection.getOutputStream();
            output.write(data.getBytes());
            output.close();

            connection.connect();
            code = connection.getResponseCode();
            body = readInputStream(connection.getInputStream());
        } catch (MalformedURLException e) {
            code = HttpURLConnection.HTTP_NOT_FOUND;
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            code = HttpURLConnection.HTTP_NOT_FOUND;
        } catch (IOException one) {
            one.printStackTrace();
            try {
                if (connection != null) {
                    code = connection.getResponseCode();
                }
            } catch (IOException two) {
                two.printStackTrace();
            }
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        Log.i(Integer.toString(code), body);
        Log.d("POST--", body);
        return (new Response(code, body));
    }


    public static Response postdv(String server, String creds, String data, String parent_dis) {
        int code = -1;
        String body = "";
        String parentdis = "";
        if (parent_dis.length() > 1) {
            parentdis = parent_dis;
        }
        HttpURLConnection connection = null;
        try {
            URL url = new URL(server);
            connection = (HttpURLConnection) url.openConnection();
            connection.setInstanceFollowRedirects(false);
            connection.setConnectTimeout(CONNECTION_TIME_OUT);
            connection.setRequestProperty("Authorization", "Basic " + creds);
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            connection.setRequestMethod("POST");
            connection.setRequestProperty("User-agent", "uphmis_aws_3009/" + System.getProperty("http.agent"));
            connection.setRequestProperty("districtuid:", parentdis);
            connection.setDoOutput(true);
            OutputStream output = connection.getOutputStream();
            output.write(data.getBytes());
            output.close();

            connection.connect();
            code = connection.getResponseCode();
            body = readInputStream(connection.getInputStream());
        } catch (MalformedURLException e) {
            code = HttpURLConnection.HTTP_NOT_FOUND;
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            code = HttpURLConnection.HTTP_NOT_FOUND;
        } catch (IOException one) {
            one.printStackTrace();
            try {
                if (connection != null) {
                    code = connection.getResponseCode();
                }
            } catch (IOException two) {
                two.printStackTrace();
            }
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        Log.i(Integer.toString(code), body);
        Log.d("POST--", body);
        return (new Response(code, body));
    }

    private static String readInputStream(InputStream stream)
            throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(stream));
        try {
            StringBuilder builder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append('\n');
            }

            return builder.toString();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isError(int code) {
        return code != HttpURLConnection.HTTP_OK;
    }

    public static String getErrorMessage(Context context, int code) {
        switch (code) {
            case HttpURLConnection.HTTP_UNAUTHORIZED:
                return context.getString(R.string.wrong_username_password);
            case HttpURLConnection.HTTP_NOT_FOUND:
                return context.getString(R.string.wrong_url);
            case HttpURLConnection.HTTP_MOVED_PERM:
                return context.getString(R.string.wrong_url);
            case HttpURLConnection.HTTP_FORBIDDEN:
                return context.getString(R.string.old_version);
            default:
                return context.getString(R.string.try_again);
        }
    }
}
