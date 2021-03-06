package org.smilec.smile.bu.json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smilec.smile.bu.Constants;
import org.smilec.smile.domain.IQSet;
import org.smilec.smile.domain.Question;

import android.util.Log;

public class IQSetJSONParser {
	
    
    private static final String TAG = "SMILE IQSetJSONParser";

    public static final boolean rowsExist(JSONObject object) {
    	
    	return object.optInt(Constants.NUMBER_OF_IQSETS) != 0? true : false;
    }
    
    /**
     * Used to parse the JSON request when we need to fill the ListView of all IQSet  #UsingPreparedQuestions
     * 
     */
    public static final List<IQSet> parseListOfIQSet(JSONObject object) {

    	List<IQSet> iqsets = new ArrayList<IQSet>();
    	
    	JSONArray rows = object.optJSONArray(Constants.ALL_THE_IQSETS);
    	
    	for(int i=0; i<rows.length();) {
    		
    		try {
				JSONObject row = rows.getJSONObject(i++);
				JSONArray value = row.getJSONArray(Constants.VALUE_OF_IQSET); 
				
				IQSet iqset = new IQSet(
						row.getString(Constants.ID_OF_IQSET),
						row.getString(Constants.KEY_OF_IQSET),
						value.getString(0),
						value.getString(1),
						value.getString(2)
						);
				
				iqsets.add(iqset);
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
    	}
    	return iqsets;
    }
    
    /**
     * Used for saving questions during a session
     */
    public static final JSONObject wrapQuestionsIntoIQSet(String nameOfIQSet, Collection<Question> questions) throws JSONException {
    	
    	JSONObject iqset = new JSONObject();
    	JSONArray iqdata = new JSONArray();
    	
    	for(int i = 0; i<questions.size();i++) {
    		
    		Question question = ((List<Question>)questions).get(i);
    		
    		// We define the type of the current question
    		String typeOfQuestion = new String();
    		typeOfQuestion = question.getImageUrl().equals("") ? "QUESTION" : "QUESTION_PIC";
    		
    		iqdata.put(new JSONObject()
				.put(Constants.NAME, question.getOwner())
				.put(Constants.IP,"127.0.0.1")
				.put(Constants.QUESTION, question.getQuestion())
				.put(Constants.OPTION_1, question.getOption1())
				.put(Constants.OPTION_2, question.getOption2())
				.put(Constants.OPTION_3, question.getOption3())
				.put(Constants.OPTION_4, question.getOption4())
				.put(Constants.TYPE, typeOfQuestion)
				.put(Constants.IMAGE_URL, question.getImageUrl())
				.put(Constants.ANSWER, question.getAnswer())
    		);
    		
    		iqset.put(Constants.TITLE_SESSION, nameOfIQSet);
    		iqset.put(Constants.IQDATA_OF_IQSET, iqdata);
    	}
    	
    	return iqset;
    }
    
    /**
     * Used to return the iqset ID when the teacher select a pool of questions  #UsingPreparedQuestions
     * 
     */
    public static final String getIdIQSetByPosition(JSONObject object, int position) {
    	
    	JSONArray rows = object.optJSONArray(Constants.ALL_THE_IQSETS);
    	
    	try {
			JSONObject row = rows.getJSONObject(position);
			return row.getString(Constants.ID_OF_IQSET);
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	return "IQSET NOT FOUND";
    }

    /**
     * Used to retrieve all the questions of a IQSet before starting GeneralActivity  #UsingPreparedQuestions
     * 
     */
    public static final Collection<Question> parseIQSet(JSONObject object) {
    	
    	Collection<Question> questions = new ArrayList<Question>();
    	JSONArray rows = object.optJSONArray(Constants.IQDATA_OF_IQSET);
    	
    	for(int i=0; i<rows.length();i++) {
    		
    		try {
				JSONObject row = rows.getJSONObject(i);

				String base64encdata = null;

                try {
                    base64encdata = row.getString(Constants.PIC);
                    Log.d(TAG, "Found PIC of size" + base64encdata.length());
                } catch(JSONException jse) {
                    Log.d(TAG, "Failed parsing JSON, reason: " + jse.getMessage());
                    // Must not have a PIC set, ignore silently
                }
				// XXX This is a dangerous way to go ... we should check all values and validity of JSON fields before we 
                // load up the Question object, otherwise, we may crash
				Question q = new Question(i,
						row.getString(Constants.NAME),
						row.getString(Constants.IP),
						row.getString(Constants.QUESTION),
						row.getString(Constants.OPTION_1),
						row.getString(Constants.OPTION_2),
						row.getString(Constants.OPTION_3),
						row.getString(Constants.OPTION_4),
						row.getInt(Constants.ANSWER),
						base64encdata);
				questions.add(q);
				
			} catch (JSONException e) {
                // XXX We should be tallying up the exceptions.
                // and log these as well, otherwise they are missed.
                Log.d(TAG, "Failed parsing JSON, reason: " + e.getMessage());
				e.printStackTrace();
			}
    	}
    	return questions;
    }
}
