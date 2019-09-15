package com.example.almocofacil.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.almocofacil.domain.Aluno;
import com.example.almocofacil.domain.Autorizacao;
import com.example.almocofacil.domain.Refeicao;
import com.example.almocofacil.domain.Requisicao;
import com.example.almocofacil.domain.enums.StatusAutorizacao;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AutorizacaoDao {
    private final String TABLE_AUTORIZACAO = "AUTORIZACAO";
    private DbGateway gw;

    public AutorizacaoDao(Context ctx){
        gw = DbGateway.getInstance(ctx);
    }

    public boolean salvar(Autorizacao autorizacao){
        try{
            ContentValues values = new ContentValues();
            values.put("ALUNONOME", autorizacao.getAluno().getNome());
            values.put("ALUNOMATRICULA", autorizacao.getAluno().getMatricula());
            values.put("DATA", autorizacao.getData().toString());
            values.put("HORA", autorizacao.getHora().toString());
            values.put("STATUSAUTORIZACAO", autorizacao.getStatusAutorizacao().getNome());
            values.put("REQUISICAOID", autorizacao.getRequisicao().getRequisicaoId());
            values.put("REFEICAOID", autorizacao.getRefeicao().getId());
            return gw.getDatabase().insertOrThrow(TABLE_AUTORIZACAO, null, values) > 0;
        } catch (SQLiteConstraintException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean excluir(int id){
        return gw.getDatabase().delete(TABLE_AUTORIZACAO, "ID=?", new String[]{ id + "" }) > 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public List<Autorizacao>listar(){
        List<Autorizacao> autorizacoes = new ArrayList<>();
        Cursor cursor = gw.getDatabase().rawQuery("SELECT * FROM AUTORIZACAO", null);

        while(cursor.moveToNext()){

            String status = cursor.getString(5);
            StatusAutorizacao statusAutorizacao = StatusAutorizacao.valueOf(status);

            Requisicao requisicao = new Requisicao();
            requisicao.setRefeicaoId(Integer.parseInt(cursor.getString(6)));

            Refeicao refeicao = new Refeicao();
            refeicao.setId(Long.parseLong(cursor.getString(7)));

            Autorizacao autorizacao = new Autorizacao();
            autorizacao.setId(cursor.getLong(0));
            autorizacao.setAluno(new Aluno(
                    cursor.getString(2), cursor.getString(1))
            );

            autorizacao.setData(LocalDate.parse(cursor.getString(3)));

            autorizacao.setHora(LocalTime.parse(cursor.getString(4)));

            autorizacao.setStatusAutorizacao(statusAutorizacao);

            autorizacao.setRequisicao(requisicao);

            autorizacao.setRefeicao(refeicao);

            autorizacoes.add(autorizacao);
        }
        cursor.close();
        return autorizacoes;
    }
}
