package cardproject.android.arnab.library;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RequestIssueBook extends StringRequest
{
    private static String REGISTER_REQUEST_URL= "http://arnabbanerjee.dx.am/RequestPostIssueBooks.php";
    private Map<String, String> params;
    int MY_SOCKET_TIMEOUT_MS=60000;

    public RequestIssueBook(ArrayList<Long> bookIds, long candidateId, String fromDate, String toDate,Response.Listener<String> listener)
    {
        super(Method.POST, REGISTER_REQUEST_URL, listener, null);

        JSONArray bookIdsJSON = new JSONArray(Arrays.asList(bookIds));


        params = new HashMap<>();
        params.put("candidateId", candidateId + "");
        params.put("bookId",bookIdsJSON.toString());
        params.put("fromDate",fromDate);
        params.put("toDate",toDate);

        this.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
