package com.example.demo.db;

import com.example.demo.Msg;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "test_table2")
public class HibMsg {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "test_table2_seq_generator"
    )
    @SequenceGenerator(
            name = "test_table2_seq_generator",
            sequenceName = "test_table2_id_seq",
            allocationSize = 1
    )
    private Long id;

    @Column(name = "f2")
    private String f2;

    @Column(name = "f3")
    private String f3;

    @Column(name = "f4")
    private Integer f4;

    @Column(name = "f5")
    private Integer f5;


    public void setId(Long id) {
        this.id = id;
    }

    public void setF2(String f2) {
        this.f2 = f2;
    }

    public void setF3(String f3) {
        this.f3 = f3;
    }

    public void setF4(Integer f4) {
        this.f4 = f4;
    }

    public void setF5(Integer f5) {
        this.f5 = f5;
    }

    public Long getId() {
        return id;
    }

    public String getF2() {
        return f2;
    }

    public String getF3() {
        return f3;
    }

    public Integer getF4() {
        return f4;
    }

    public Integer getF5() {
        return f5;
    }

    public static List<HibMsg> generateRnd(int n) {
        var list = new ArrayList<HibMsg>(n);
        for (int i = 0; i < n; i++) {
            var msg = new HibMsg();
            msg.setF2(Msg.generate(10));
            msg.setF3(Msg.generate(10));
            msg.setF4(Msg.generateInt());
            msg.setF5(Msg.generateInt());
            list.add(msg);
        }
        return list;
    }
}
