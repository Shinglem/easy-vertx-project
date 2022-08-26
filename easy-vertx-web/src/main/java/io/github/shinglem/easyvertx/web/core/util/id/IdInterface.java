package io.github.shinglem.easyvertx.web.core.util.id;

public interface IdInterface {

    public static IdInterface create(){
        return new SnowFlake(1, 1);
    }

    public long nextId() ;
}
