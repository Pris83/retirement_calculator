import { Component } from '@angular/core';
import { RetirementCalculatorComponent } from './components/retirement-calculator/retirement-calculator.component';
import { HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RetirementCalculatorComponent,HttpClientModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  title = 'retirementCalculatorFrontend';
}
