package service;

import dataAccess.DataAccessClear;
import dataAccess.DataAccessException;
import model.AuthData;

public class clear<T extends DataAccessClear> implements DataAccessClear {
    private T data;

    public clear(T data) {
        this.data = data;
    }





    @Override
    public void clear() throws DataAccessException {
        // Implement clear method if needed
    }
}
