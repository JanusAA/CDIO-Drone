package QR;

import com.google.zxing.Result;

public interface QRListener {

	public void onTag(Result result, float orientation);
}


