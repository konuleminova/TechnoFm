package app.android.jazzfm.oidarfm.fragment;

import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import app.android.jazzfm.oidarfm.R;
import static app.android.jazzfm.oidarfm.fragment.FmTab.isNetworkAvailable;

public class InfoTab extends Fragment {
    private AdView mAdView;
    String text = null, response = null;
    Editable holdEmail, holdMessage;
    EditText getEmail, getMessage;
    String version, PACKAGE_NAME, mButtonText;
    Button send;
    AlertDialog alertDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        try {
            getEmail = (EditText) view.findViewById(R.id.email_layout);
            getMessage = (EditText) view.findViewById(R.id.message_layout);
            holdEmail = getEmail.getText();
            holdMessage = getMessage.getText();
            send = (Button) view.findViewById(R.id.btnSend);
            mButtonText = (String) send.getText();
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            version = pInfo.versionName;
            PACKAGE_NAME = getActivity().getApplicationContext().getPackageName();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkAvailable(getActivity()) == true) {
                    try {
                        send.setClickable(false);
                        SendPostRequest task = new SendPostRequest();
                        task.execute();
                    } catch (Exception ex) {
                        Toast.makeText(getActivity(), getResources().getString(R.string.internet), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.internet), Toast.LENGTH_LONG).show();
                }
            }
        });

        mAdView = (AdView) view.findViewById(R.id.adView);
        MobileAds.initialize(getContext(), getResources().getString(R.string.addcontext));
        AdRequest request = new AdRequest.Builder()
                .addTestDevice(getResources().getString(R.string.adddevice))
                .build();
        mAdView.loadAd(request);
        return view;
    }

    public class SendPostRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute() {
            send.setClickable(false);
            send.setText("Sending... ");


        }

        protected String doInBackground(String... arg0) {
            try {
                URL url = new URL(getResources().getString(R.string.api));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                JSONObject postDataParams = new JSONObject();
                postDataParams.put("email", holdEmail);
                postDataParams.put("message", holdMessage);
                postDataParams.put("app_version", version);
                postDataParams.put("app_id", PACKAGE_NAME);
                postDataParams.put("api_key", "5a1ea4bcbceb9");

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(postDataParams.toString());
                writer.flush();
                writer.close();
                os.close();
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    BufferedReader in = new BufferedReader(new
                            InputStreamReader(
                            conn.getInputStream()));

                    StringBuffer sb = new StringBuffer("");
                    String line = "";

                    while ((line = in.readLine()) != null) {

                        sb.append(line);

                        break;

                    }
                    in.close();
                    text = sb.toString();
                    JSONObject jsonObj = new JSONObject(text);
                    String code = jsonObj.getString("code");
                    response = code.toString();
                    return sb.toString();


                } else {

                    return new String("false : " + responseCode);
                }
            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }


        }

        @Override
        protected void onPostExecute(String result) {
            send.setClickable(true);
            send.setText(mButtonText);
            if (response.equals("1000")) {
                setAlertDialog(getResources().getString(R.string.thank));

            } else if (response.equals("1010")) {
                setAlertDialog(getResources().getString(R.string.fill));

            } else if (response.equals("1001")) {

                setAlertDialog(getResources().getString(R.string.db));
            } else if (response.equals("1009")) {
                setAlertDialog(getResources().getString(R.string.get));

            } else if (response.equals("1002")) {
                setAlertDialog(getResources().getString(R.string.keyeror));
            } else if (response.equals("1003")) {
                setAlertDialog(getResources().getString(R.string.key));

            }

        }
    }

    String mMessage;
    private void setAlertDialog(String message) {
        this.mMessage = message;
        alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

}