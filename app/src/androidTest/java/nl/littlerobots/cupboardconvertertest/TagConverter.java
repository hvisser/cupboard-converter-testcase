package nl.littlerobots.cupboardconvertertest;

import android.content.ContentValues;
import android.database.Cursor;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import nl.littlerobots.cupboardconvertertest.model.Tag;
import nl.qbusict.cupboard.Cupboard;
import nl.qbusict.cupboard.convert.FieldConverter;
import nl.qbusict.cupboard.convert.ReflectiveEntityConverter;

public class TagConverter extends ReflectiveEntityConverter<Tag> {
    public TagConverter(Cupboard cupboard) {
        super(cupboard, Tag.class);
    }

    @Override
    protected FieldConverter<?> getFieldConverter(Field field) {
        switch (field.getName()) {
            case "ids":
                return new FieldConverter<List<String>>() {
                    @Override
                    public List<String> fromCursorValue(Cursor cursor, int columnIndex) {
                        return Arrays.asList(cursor.getString(columnIndex).split(","));
                    }

                    @Override
                    public void toContentValue(List<String> value, String key, ContentValues values) {

                    }

                    @Override
                    public ColumnType getColumnType() {
                        return ColumnType.JOIN;
                    }
                };
            default:
                return super.getFieldConverter(field);
        }
    }
}
