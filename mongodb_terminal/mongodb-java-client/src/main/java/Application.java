import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import static com.mongodb.client.model.Filters.eq;

public class Application {

    public static final String MONGO_DB_URL = "mongodb://127.0.0.1:27017,127.0.0.1:27018,127.0.0.1:27019/?replicaSet=rs0";
    public static final  String DB_NAME = "online-school";
    public static final double MIN_GPA =   90.0;

    public static void main(String[] args) {
        String courseName = args[0];
        String studentName = args[1];
        int age = Integer.parseInt(args[2]);
        double gpa = Double.parseDouble(args[3]);

        MongoDatabase onlineSchoolDB = connectToMongoDB(MONGO_DB_URL, DB_NAME);
        enroll(onlineSchoolDB, courseName, studentName, age, gpa);
    }

    private static void enroll(MongoDatabase database, String course, String studentName, int age, double gpa){
        if(!isValidCourse(database, course)){
            System.out.println("Invalid course "+ course);
            return;
        }

        MongoCollection<Document> courseCollection = database.getCollection(course)
                .withWriteConcern(WriteConcern.MAJORITY)
                .withReadPreference(ReadPreference.primaryPreferred());

        //check if student is already present
        if(courseCollection.find(eq("name", studentName)).first() != null){
            System.out.println("Student "+ studentName + " is already enrolled");
            return;
        }

        //check gpa
        if(gpa < MIN_GPA){
            System.out.printf("Please improve student gpa");
            return;
        }

        courseCollection.insertOne(new Document("name", studentName).append("age", age).append("gpa", gpa));
        System.out.println("Student "+ studentName + "was successfully enrolled in "+ course);

        for(Document document: courseCollection.find()){
            System.out.println(document);
        }
    }

    private static boolean isValidCourse(MongoDatabase db, String course){
        for (String collectionName: db.listCollectionNames()){
            if(collectionName.equals(course)){
                return true;
            }
        }
        return false;
    }

    public static MongoDatabase connectToMongoDB(String url, String dbName){

        MongoClient mongoClient = new MongoClient(new MongoClientURI(url)) {
        };
        return mongoClient.getDatabase(dbName);
    }

}
