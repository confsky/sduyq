package sduyq.demo.model;


public class Youhao {
//	@Excel(name = "底盘号")
    private String chassis;
	
//	@Excel(name = "每日平均油耗")
    private String avg_fuel;
	
	public Youhao() {}

	public Youhao(String chassis, String avg_fuel) {
		super();
		this.chassis = chassis;
		this.avg_fuel = avg_fuel;
	}

	public String getChassis() {
		return chassis;
	}

	public void setChassis(String chassis) {
		this.chassis = chassis;
	}

	public String getAvg_fuel() {
		return avg_fuel;
	}

	public void setAvg_fuel(String avg_fuel) {
		this.avg_fuel = avg_fuel;
	}

}
