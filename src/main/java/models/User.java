package models;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@RequiredArgsConstructor
public final class User implements Serializable {
    private static final long serialVersionUID = 3848335680382830605L;

    public final String username, role, password;
    public final Timestamp created_at, updated_at;
}
