package com.ourslook.guower.service.impl.score;

import com.ourslook.guower.dao.score.InfScoreDayMaxDao;
import com.ourslook.guower.entity.score.InfScoreDayMaxEntity;
import com.ourslook.guower.service.score.InfScoreDayMaxService;
import com.ourslook.guower.utils.Constant;
import com.ourslook.guower.utils.DateUtils;
import com.ourslook.guower.utils.beanmapper.BeanMapper;
import com.ourslook.guower.utils.office.csvexports.CsvExportor;
import com.ourslook.guower.utils.office.excelutils.ExcelColumCellInterface;
import com.ourslook.guower.utils.office.excelutils.ExcelColumn;
import com.ourslook.guower.utils.office.excelutils.ExcelHead;
import com.ourslook.guower.utils.office.excelutils.ExcelHelper;
import org.apache.poi.ss.usermodel.CellType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Transactional
@Service("infScoreDayMaxService")
public class InfScoreDayMaxServiceImpl implements InfScoreDayMaxService {
    private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private InfScoreDayMaxDao infScoreDayMaxDao;
    @Resource
    private BeanMapper beanMapper;
    @Resource
    private CsvExportor csvExportor;
    @Resource
    private MessageSource messageSource;

	@Override
	public InfScoreDayMaxEntity queryObject(Integer id){
		return infScoreDayMaxDao.queryObject(id);
	}
	
	@Override
	public List<InfScoreDayMaxEntity> queryList(Map<String, Object> map){
		return infScoreDayMaxDao.queryList(map);
	}
	
	@Override
	public int queryTotal(Map<String, Object> map){
		return infScoreDayMaxDao.queryTotal(map);
	}
	
	@Override
	public void save(InfScoreDayMaxEntity infScoreDayMax){
		infScoreDayMaxDao.save(infScoreDayMax);
	}

    @Override
    public void multiOperate(List<String> modelIds, Integer status) {
//        if (XaUtils.isNotEmpty(modelIds)) {
//            status = status == null ? Constant.Status.delete : status;
//            Query query = new Query();
//            query.put("id_IN", modelIds);
//            List<InfScoreDayMaxEntity> entities =  infScoreDayMaxDao.queryList(query);
//            for (int i = 0, count = entities.size(); i < count; ++i) {
//                InfScoreDayMaxEntity entity = entities.get(i);
//                entity.setState(status);
//                infScoreDayMaxDao.update(entity);
//            }
//        }
    }

	@Override
	public void update(InfScoreDayMaxEntity infScoreDayMax){
        Objects.requireNonNull(infScoreDayMax);
        final InfScoreDayMaxEntity dest = new InfScoreDayMaxEntity();
        Optional.ofNullable(infScoreDayMax.getId()).ifPresent(aInteger -> {
            InfScoreDayMaxEntity dests = Optional.ofNullable(infScoreDayMaxDao.queryObject(aInteger)).orElse(dest);
            beanMapper.copy(infScoreDayMax, dests);
            infScoreDayMaxDao.update(dests);
        });
	}


	@Override
	public void delete(Integer id){
		infScoreDayMaxDao.delete(id);
	}
	
	@Override
	public void deleteBatch(Integer[] ids){
		infScoreDayMaxDao.deleteBatch(ids);
	}

    /**
    * 导出成为excel
    * <p>
    * 导出数据不正确的几种情况分析
    * 1：要检查CellType是否使用正确，如要显示字符串您确使用 CellType.NUMERIC
    * 2：去确定转换函数（excelColumnsConvertMap）的泛型，如数据是整数Map<Integer,String>您确使用了Map<String,String>
    */
    @Override
    @SuppressWarnings("all")
    public void exportsToExcels(List<InfScoreDayMaxEntity> vos, HttpServletRequest request, HttpServletResponse response , boolean isCvs) throws Exception {

        //excel结构
        List<ExcelColumn> excelColumns = new ArrayList<>();

                                    excelColumns.add(new ExcelColumn(excelColumns.size(), "id", "编号", CellType.NUMERIC));
                                                excelColumns.add(new ExcelColumn(excelColumns.size(), "getType", "获取途径", CellType.NUMERIC));
                                                excelColumns.add(new ExcelColumn(excelColumns.size(), "getNumber", "单次获取量", CellType.NUMERIC));
                                                excelColumns.add(new ExcelColumn(excelColumns.size(), "getMax", "单日获取上限", CellType.NUMERIC));
                    
        // 需要特殊转换的单元 map的可以必须好实体的类型一致
		// 一下excelColumnsConvertMap 转换的根据实际情况使用，这里是示例
        Map<String, Map> excelColumnsConvertMap = new HashMap<>();

        Map<String, String> isReceive1 = new HashMap<>();
        /**
         * {@link Constant.OrderStatus}
         */
         /*
        isReceive1.put("0", "未支付");
        isReceive1.put("1", "已支付");
        isReceive1.put("2", "待收货");
        isReceive1.put("3", "已经消费/已经收货");
        isReceive1.put("4", "交易完成");
        isReceive1.put("5", "交易关闭");
        excelColumnsConvertMap.put("orderState", isReceive1);*/

        /**
         * {@link Constant.PayType}
         */
        /*Map<Integer, String> isReceive2 = new HashMap<>();
        try {
            Map<Integer, String> map = ReflectUtils.covetConstaintClassToMap(Constant.PayType.class);
            isReceive2.putAll(map);
            excelColumnsConvertMap.put("paymentKind", isReceive2);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RRException(e.getLocalizedMessage());
        }*/

        //转换函数
        Map<String, ExcelColumCellInterface> excelColumnsConvertFunMap = new HashMap<>();
        //excelColumnsConvertFunMap.put("orderState", new ExcelHead.ExcelColumCellDate());
        //excelColumnsConvertFunMap.put("clearingmoney", new ExcelHead.ExcelColumCellBigDecimal("元"));

        //class类型抓换函数
        //Map<Class, ExcelColumCellInterface> columnsTypeConvertFunMap = new HashMap<>();
        //columnsTypeConvertFunMap.put(LocalDateTime.class, new ExcelHead.ExcelColumCellLocalDateTime());

        //设置头部
        ExcelHead head = new ExcelHead();
        head.setRowCount(1); // 模板中头部所占行数
        head.setColumns(excelColumns);  // 列的定义
        head.setColumnsConvertMap(excelColumnsConvertMap); // 列的转换byMap
        head.setColumnsConvertFunMap(excelColumnsConvertFunMap); // 列的转换by 接口方法
        //执行导出,第一个null是response参数，用来输出到浏览器，第二个null是要导出的数据集合
        if (isCvs) {
            ExcelHelper.getInstanse().exportCsvFile(csvExportor, request, response, head, vos,
                    "单日积分获取上限表-" + DateUtils.parseDateTime(new Date(), DateUtils.YYYYMMDDHHMMSSCn));
        } else {
            ExcelHelper.getInstanse().exportExcelFile(request, response, head, vos,
                    "单日积分获取上限表-" + DateUtils.parseDateTime(new Date(), DateUtils.YYYYMMDDHHMMSSCn));
        }

    }

}
