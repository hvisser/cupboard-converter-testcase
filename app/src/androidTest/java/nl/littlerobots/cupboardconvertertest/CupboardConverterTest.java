package nl.littlerobots.cupboardconvertertest;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.test.AndroidTestCase;

import java.util.Arrays;
import java.util.List;

import nl.littlerobots.cupboardconvertertest.model.Tag;
import nl.qbusict.cupboard.Cupboard;
import nl.qbusict.cupboard.CupboardBuilder;
import nl.qbusict.cupboard.convert.EntityConverter;
import nl.qbusict.cupboard.convert.EntityConverterFactory;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;
import static nl.qbusict.cupboard.CupboardFactory.setCupboard;

public class CupboardConverterTest extends AndroidTestCase {

    static {
        setCupboard(new CupboardBuilder().registerEntityConverterFactory(new EntityConverterFactory() {
            @Override
            public <T> EntityConverter<T> create(Cupboard cupboard, Class<T> type) {
                if (type == Tag.class) {
                    return (EntityConverter<T>) new TagConverter(cupboard);
                }
                return null;
            }
        }).build());
        cupboard().register(Tag.class);
    }

    public void testValidateTableStructure() {
        OpenHelper helper = new OpenHelper(getContext());
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("pragma table_info('Tag')", null);
        try {
            assertEquals(3, cursor.getCount());
        } finally {
            cursor.close();
        }
    }

    public void testBasicMapping() {
        OpenHelper helper = new OpenHelper(getContext());
        SQLiteDatabase db = helper.getWritableDatabase();
        Tag tag = new Tag();
        tag.name = "test";
        tag.ids = Arrays.asList("unused");
        cupboard().withDatabase(db).put(tag);
        Tag stored = cupboard().withDatabase(db).query(Tag.class).get();
        assertEquals(tag.name, stored.name);
    }

    public void testConcatMapping() {
        OpenHelper helper = new OpenHelper(getContext());
        SQLiteDatabase db = helper.getWritableDatabase();
        Tag tag = new Tag();
        tag.name = "test";
        tag.bookId = 1;
        cupboard().withDatabase(db).put(tag);
        tag = new Tag();
        tag.name = "test";
        tag.bookId = 2;
        cupboard().withDatabase(db).put(tag);
        List<Tag> tags = cupboard().withDatabase(db).query(Tag.class).withProjection("*,group_concat(bookid) as ids").list();
        assertNotNull(tags);
        assertEquals(1, tags.size());
        assertEquals(2, tags.get(0).ids.size());
    }

    private static class OpenHelper extends SQLiteOpenHelper {

        public OpenHelper(Context context) {
            super(context, null, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            cupboard().withDatabase(db).createTables();
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}
