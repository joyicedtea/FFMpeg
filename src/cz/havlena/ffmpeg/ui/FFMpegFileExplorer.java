package cz.havlena.ffmpeg.ui;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

import com.media.ffmpeg.FFMpeg;

import cz.havlena.android.ui.MessageBox;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class FFMpegFileExplorer extends ListActivity {

	private static final String TAG = "FFMpegFileExplorer";
	
	private String 			mRoot = "/sdcard";
	private TextView 		mTextViewLocation;
	private File[]			mFiles;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.ffmpeg_file_explorer);
		mTextViewLocation = (TextView) findViewById(R.id.textview_path);
		getDirectory(mRoot);
	}
	
	protected static boolean checkExtension(File file) {
		String[] exts = FFMpeg.EXTENSIONS;
		for(int i=0;i<exts.length;i++) {
			if(file.getName().indexOf(exts[i]) > 0) {
				return true;
			}
		}
		return false;
	}
	
	private void sortFilesByDirectory(File[] files) {
		Arrays.sort(files, new Comparator<File>() {

			public int compare(File f1, File f2) {
				return Long.valueOf(f1.length()).compareTo(f2.length());
			}
			
		});
	}

	private void getDirectory(String dirPath) {
		try {
			mTextViewLocation.setText("Location: " + dirPath);
	
			File f = new File(dirPath);
			File[] temp = f.listFiles();
			
			sortFilesByDirectory(temp);
			
			File[] files = null;
			if(!dirPath.equals(mRoot)) {
				files = new File[temp.length + 1];
				System.arraycopy(temp, 0, files, 1, temp.length);
				files[0] = new File(f.getParent());
			} else {
				files = temp;
			}
			
			mFiles = files;
			setListAdapter(new FileExplorerAdapter(this, files, temp.length == files.length));
		} catch(Exception ex) {
			MessageBox.show(this, "Error", ex.getMessage());
		}
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		File file = mFiles[position];

		if (file.isDirectory()) {
			if (file.canRead())
				getDirectory(file.getAbsolutePath());
			else {
				MessageBox.show(this, "Error", "[" + file.getName() + "] folder can't be read!");
			}
		} else {
			if(!checkExtension(file)) {
				StringBuilder strBuilder = new StringBuilder();
				for(int i=0;i<FFMpeg.EXTENSIONS.length;i++)
					strBuilder.append(FFMpeg.EXTENSIONS[i] + " ");
				MessageBox.show(this, "Error", "File must have this extensions: " + strBuilder.toString());
				return;
			}
			Intent i = new Intent(FFMpegFileExplorer.this, FFMpegActivity.class);
			i.putExtra(FFMpegActivity.FILE_INPUT, file.getAbsolutePath());
			setResult(RESULT_OK);
			i.setAction(Intent.ACTION_INPUT_METHOD_CHANGED);
			finish();
			startActivity(i);
		}
	}
	
}
