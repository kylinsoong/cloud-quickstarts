package io.cloudadc.quiz.questions.loader;


import io.cloudadc.quiz.model.Question;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.StructuredQuery;

public class QuestionsLoader {
	
	public static QuestionsLoader create() {
		return new QuestionsLoader();
	}
	
	Logger log = LoggerFactory.getLogger(Main.class);
	

    private Datastore datastore = DatastoreOptions.newBuilder().build().getService();
    
    private static final String ENTITY_KIND = "Question";
    
    private final KeyFactory keyFactory = datastore.newKeyFactory().setKind(ENTITY_KIND);

    public Key createQuestion(Question question){
    	
        Key key = datastore.allocateId(keyFactory.newKey());
        
        Entity questionEntity = Entity.newBuilder(key)
                .set(Question.QUIZ, question.getQuiz())
                .set(Question.AUTHOR, question.getAuthor())
                .set(Question.TITLE, question.getTitle())
                .set(Question.ANSWER_ONE,question.getAnswerOne())
                .set(Question.ANSWER_TWO, question.getAnswerTwo())
                .set(Question.ANSWER_THREE,question.getAnswerThree())
                .set(Question.ANSWER_FOUR, question.getAnswerFour())
                .set(Question.CORRECT_ANSWER, question.getCorrectAnswer())
                .set(Question.IMAGE_URL,question.getImageUrl())
                .build();
        
        datastore.put(questionEntity);
        
        log.info("insert quiz " + question.getQuiz() + "/" + question.getTitle() + ", " + key);
        
        return key;
    }

    public List<Question> getAllQuestions(String quiz){
    	
        Query<Entity> query = Query.newEntityQueryBuilder()
                .setKind(ENTITY_KIND)
                .setFilter(StructuredQuery.PropertyFilter.eq(Question.QUIZ, quiz))
                .build();
        
        Iterator<Entity> entities = datastore.run(query);
        
        List<Question> list = buildQuestions(entities);
        
        log.info("query quiz " + quiz + ", total size: " + list.size());
        
        return list;
    }

    private List<Question> buildQuestions(Iterator<Entity> entities){
    	
        List<Question> questions = new ArrayList<>();
        entities.forEachRemaining(entity-> questions.add(entityToQuestion(entity)));
        return questions;
    }

    private Question entityToQuestion(Entity entity){
    	
        return new Question.Builder()
                .withQuiz(entity.getString(Question.QUIZ))
                .withAuthor(entity.getString(Question.AUTHOR))
                .withTitle(entity.getString(Question.TITLE))
                .withAnswerOne(entity.getString(Question.ANSWER_ONE))
                .withAnswerTwo(entity.getString(Question.ANSWER_TWO))
                .withAnswerThree(entity.getString(Question.ANSWER_THREE))
                .withAnswerFour(entity.getString(Question.ANSWER_FOUR))
                .withCorrectAnswer(entity.getLong(Question.CORRECT_ANSWER))
                .withImageUrl(entity.getString(Question.IMAGE_URL))
                .withId(entity.getKey().getId())
                .build();
    }
}
